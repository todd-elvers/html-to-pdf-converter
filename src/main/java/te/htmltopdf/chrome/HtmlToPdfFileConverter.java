package te.htmltopdf.chrome;

import te.htmltopdf.domain.PdfFile;

import java.io.File;

class HtmlToPdfFileConverter {

    private ChromePdfFileGenerator chromePdfGenerator;

    PdfFile convert(String html) {
        ChromePdfOptions options = new ChromePdfOptions.Builder()
                .setDisplayHeaderFooter(true)
                .setPrintBackground(true)
                .setScale(1d)
                .setPaperWidth(8.5d)
                .setPaperHeight(11d)
                .setMarginTop(0.4d)
                .setMarginBottom(0.4d)
                .setMarginLeft(0.4d)
                .setMarginRight(0.4d)
                .setPageRanges("")
                .setIgnoreInvalidPageRanges(false)
                .setHeaderTemplate(" ")
                .setFooterTemplate(" ")
                .setPreferCSSPageSize(false)
                .build();


        return chromePdfGenerator.generate(options, new File("some-file"));
    }
}