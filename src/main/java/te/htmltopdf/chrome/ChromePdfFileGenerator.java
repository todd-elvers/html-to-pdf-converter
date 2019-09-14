package te.htmltopdf.chrome;

import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import io.vavr.control.Try;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.chrome.domain.InMemoryWritablePDF;
import te.htmltopdf.chrome.domain.OptionsForPDF;

public class ChromePdfFileGenerator {

    private static final Logger log = LoggerFactory.getLogger(ChromePdfFileGenerator.class);

    private final ChromeServiceWrapper chromeServiceWrapper;

    public ChromePdfFileGenerator() {
        this.chromeServiceWrapper = new ChromeServiceWrapper();
    }

    public ChromePdfFileGenerator(ChromeServiceWrapper chromeServiceWrapper) {
        this.chromeServiceWrapper = chromeServiceWrapper;
    }

    public InMemoryWritablePDF generate(File htmlFile, OptionsForPDF options) {
        log.debug("Connecting to Chrome process.");

        String pdfContents = chromeServiceWrapper.doInChromeTab((chromeTab, chromeDevTools) ->
            blockingCallToGeneratePDF(chromeDevTools, htmlFile, options)
        );

        return new InMemoryWritablePDF(pdfContents);
    }

    protected String blockingCallToGeneratePDF(
        ChromeDevToolsService devTools,
        File htmlFile,
        OptionsForPDF options
    ) {
        // Fetch a reference to the page inside of our headless Chrome tab
        Page page = devTools.getPage();
        page.enable();

        // Register event listener: when the page finishes loading then print the contents as PDF
        AtomicReference<String> asyncResult = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        page.onLoadEventFired(event -> {
            asyncResult.set(printToPDF(page, options));
            countDownLatch.countDown();
        });

        // Load our HTML file onto the page & wait for PDF generation to complete
        page.navigate("file://" + htmlFile.getAbsolutePath());
        Try.run(countDownLatch::await);

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