package te.htmltopdf.chrome;


import com.github.kklisura.cdt.protocol.commands.Page;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.types.ChromeTab;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.TempFileGenerator;
import te.htmltopdf.domain.PdfFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Base64;

class ChromePdfFileGenerator {
    private static final Logger log = LoggerFactory.getLogger(ChromePdfFileGenerator.class);

    private ChromeServiceWrapper chromeServiceWrapper;
    private TempFileGenerator tempFileGenerator;    // TODO: add use case for the temp file generation here

    PdfFile generate(ChromePdfOptions options, File htmlFile) {
        ChromeTab chromeTab = chromeServiceWrapper.getChromeTab();
        ChromeDevToolsService devToolsService = chromeServiceWrapper.getChromeDevToolsService(chromeTab);

        File rawPdfFile = Try
                .of(() -> File.createTempFile("report", ".pdf"))
                .get();

        try {
//            chromeTab = chromeServiceWrapper.getChromeTab();
//            devToolsService = chromeServiceWrapper.getChromeDevToolsService(chromeTab);
            devToolsService.getEmulation().setEmulatedMedia("print");

            Page page = devToolsService.getPage();
            page.enable();
            page.navigate("file://" + htmlFile.getAbsolutePath());
            page.onLoadEventFired(event -> {
                String pdfFileContents = printPdfToString(page, options);
                try {
                    writeContentsToFile(pdfFileContents, rawPdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                devToolsService.close();
            });
        } catch (Exception e) {
            log.error("Failed to write PDF contents to file.");
        } finally {
            if (devToolsService != null) {
                devToolsService.waitUntilClosed();
            }
            chromeServiceWrapper.closeTab(chromeTab);
            htmlFile.delete();
        }
        log.debug("PDF generation complete.");
        return new PdfFile(rawPdfFile);
    }

    protected String printPdfToString(Page page, ChromePdfOptions options) {
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

    protected void writeContentsToFile(String data, File file) throws IOException {
        try (OutputStream stream = Files.newOutputStream(file.toPath())) {
            stream.write(Base64.getDecoder().decode(data));
        }
    }
}