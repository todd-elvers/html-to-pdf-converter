//package te.htmltopdf.chrome;
//
//
//import groovy.transform.CompileStatic
//import groovy.util.logging.Slf4j
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.stereotype.Component
//
//@Slf4j
//@CompileStatic
//@Component
//class HtmlToPdfFileConverter {
//
//    @Autowired
//    ChromePdfFileGenerator chromePdfGenerator
//
//    @Autowired
//    HbvHtmlFileGenerator htmlFileGenerator
//
//    PdfFile convert(String html) {
//        ChromePdfOptions options = [
//        landscape              : false,
//                displayHeaderFooter    : true,
//                printBackground        : true,
//                scale                  : 1d,
//                paperWidth             : 8.5d,
//                paperHeight            : 11d,
//                marginTop              : 0.4d,
//                marginBottom           : 0.4d,
//                marginLeft             : 0.4d,
//                marginRight            : 0.4d,
//                pageRanges             : "",
//                ignoreInvalidPageRanges: false,
//                headerTemplate         : " ",
//                footerTemplate         : " ",
//                preferCSSPageSize      : false,
//        ]
//
//        File tempHtmlFile = htmlFileGenerator.generate(html)
//        return chromePdfGenerator.generate(options, tempHtmlFile)
//    }
//}