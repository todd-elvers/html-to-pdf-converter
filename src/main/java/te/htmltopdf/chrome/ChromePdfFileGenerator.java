package te.htmltopdf.chrome;

import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import io.vavr.control.Try;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.chrome.domain.ChromePdfOptions;
import te.htmltopdf.chrome.domain.WritablePDFContents;

public class ChromePdfFileGenerator {
    private static final Logger log = LoggerFactory.getLogger(ChromePdfFileGenerator.class);

    private final ChromeServiceWrapper chromeServiceWrapper;

    public ChromePdfFileGenerator() {
        this.chromeServiceWrapper = new ChromeServiceWrapper();
    }

    public ChromePdfFileGenerator(ChromeServiceWrapper chromeServiceWrapper) {
        this.chromeServiceWrapper = chromeServiceWrapper;
    }

    public WritablePDFContents generate(ChromePdfOptions options, File htmlFile) {
        log.debug("Connecting to Chrome process.");

        String pdfContents = chromeServiceWrapper.doInChromeTab((chromeTab, chromeDevTools) ->
            blockingCallToGeneratePDF(chromeDevTools, htmlFile, options)
        );

        return new WritablePDFContents(pdfContents);
    }

    protected String blockingCallToGeneratePDF(ChromeDevToolsService devTools, File htmlFile, ChromePdfOptions options) {
        AtomicReference<String> asyncResult = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // Navigate headless Chrome to the local HTML file
        Page page = devTools.getPage();
        page.enable();
        page.navigate("file://" + htmlFile.getAbsolutePath());

        // ASYNC: once the page finishes loading print its contents as PDF
        page.onLoadEventFired(event -> {
            log.debug("onLoadEventFired() - page has completed loading.");
            asyncResult.set(printToPDF(page, options));
            countDownLatch.countDown();
        });

        // Wait for the page to the load
        log.debug("Waiting for response from Chrome tab (via CountDownLatch).");
        Try.run(countDownLatch::await);

        return asyncResult.get();
    }

    protected String printToPDF(Page page, ChromePdfOptions options) {
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