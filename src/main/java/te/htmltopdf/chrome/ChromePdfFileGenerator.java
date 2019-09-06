//package te.htmltopdf.chrome;
//
//
//import com.github.kklisura.cdt.protocol.commands.Page
//import com.github.kklisura.cdt.protocol.events.page.LoadEventFired
//import com.github.kklisura.cdt.protocol.support.types.EventHandler
//import com.github.kklisura.cdt.services.ChromeDevToolsService
//import com.github.kklisura.cdt.services.types.ChromeTab
//import groovy.transform.CompileStatic
//import groovy.util.logging.Slf4j
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//
//@Slf4j
//@SuppressWarnings("GrMethodMayBeStatic")
//@CompileStatic
//@Component
//class ChromePdfFileGenerator {
//
//    @Autowired
//    ChromeServiceWrapper chromeServiceWrapper
//
//    PdfFile generate(ChromePdfOptions options, File htmlFile) {
//        ChromeTab chromeTab = null
//        ChromeDevToolsService devToolsService = null
//
//        File rawPdfFile = File.createTempFile("report", ".pdf")
//        try {
//            chromeTab = chromeServiceWrapper.getChromeTab()
//            devToolsService = chromeServiceWrapper.getChromeDevToolsService(chromeTab)
//            devToolsService.emulation.emulatedMedia = 'print'
//
//            Page page = devToolsService.getPage()
//            page.enable()
//            page.navigate('file://' + htmlFile.absolutePath)
//            page.onLoadEventFired(new EventHandler<LoadEventFired>() {
//                @Override
//                void onEvent(LoadEventFired event) {
//                    String pdfFileContents = printPdfToString(page, options)
//                    writeContentsToFile(pdfFileContents, rawPdfFile)
//                    devToolsService?.close()
//                }
//            })
//        } finally {
//            devToolsService?.waitUntilClosed()
//            chromeServiceWrapper.closeTab(chromeTab)
//            htmlFile.delete()
//        }
//        log.debug("PDF generation complete.")
//        return new PdfFile(rawPdfFile)
//    }
//
//    protected String printPdfToString(Page page, ChromePdfOptions options) {
//        return page.printToPDF(
//                options.landscape,
//                options.displayHeaderFooter,
//                options.printBackground,
//                options.scale,
//                options.paperWidth,
//                options.paperHeight,
//                options.marginTop,
//                options.marginBottom,
//                options.marginLeft,
//                options.marginRight,
//                options.pageRanges,
//                options.ignoreInvalidPageRanges,
//                options.headerTemplate,
//                options.footerTemplate,
//                options.preferCSSPageSize
//        )
//    }
//
//    protected void writeContentsToFile(String data, File file) {
//        file.withOutputStream { outputStream ->
//                outputStream.write(Base64.decoder.decode(data))
//        }
//    }
//}