package te.htmltopdf.chrome;

import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.chrome.domain.InMemoryPDF;
import te.htmltopdf.chrome.domain.OptionsForPDF;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ChromePdfFileGenerator {
    private static final Logger log = LoggerFactory.getLogger(ChromePdfFileGenerator.class);
    private static final int TIMEOUT_SECONDS = 10;

    private final ChromeServiceWrapper chromeServiceWrapper;

    public ChromePdfFileGenerator() {
        this.chromeServiceWrapper = new ChromeServiceWrapper();
    }

    public ChromePdfFileGenerator(ChromeServiceWrapper chromeServiceWrapper) {
        this.chromeServiceWrapper = chromeServiceWrapper;
    }

    public InMemoryPDF generate(File htmlFile, OptionsForPDF options) {
        log.debug("Connecting to Chrome process.");

        String pdfContents = chromeServiceWrapper.doInChromeTab((chromeTab, chromeDevTools) ->
                blockingCallToGeneratePDF(chromeDevTools, htmlFile, options)
        );

        return new InMemoryPDF(pdfContents);
    }

    protected String blockingCallToGeneratePDF(
            ChromeDevToolsService devTools,
            File htmlFile,
            OptionsForPDF options
    ) {
        // Fetch a reference to the page inside of our headless Chrome tab
        Page page = devTools.getPage();
        page.enable();

        // Navigate to our HTML file on disk
        page.navigate("file://" + htmlFile.getAbsolutePath());

        // Register event listener: when the page finishes loading print the contents as PDF
        AtomicReference<String> asyncResult = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        page.onLoadEventFired(event -> {
            asyncResult.set(printToPDF(page, options));
            countDownLatch.countDown();
        });

        // Throws InterruptedException wrapped as a RuntimeException if the above deadlocks
        Try.run(() -> countDownLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));

        return asyncResult.get();
    }

    protected String printToPDF(Page page, OptionsForPDF options) {
        return page.printToPDF(
                options.getLandscape(),
                options.getDisplayHeaderFooter(),
                options.getPrintBackground(),
                options.getScale(),
                options.getPaperWidth(),
                options.getPaperHeight(),
                options.getMarginTop(),
                options.getMarginBottom(),
                options.getMarginLeft(),
                options.getMarginRight(),
                options.getPageRanges(),
                options.getIgnoreInvalidPageRanges(),
                options.getHeaderTemplate(),
                options.getFooterTemplate(),
                options.getPreferCSSPageSize()
        );
    }

}