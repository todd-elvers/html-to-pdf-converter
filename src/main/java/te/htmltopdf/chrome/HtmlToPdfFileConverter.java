package te.htmltopdf.chrome;

import java.io.File;
import te.htmltopdf.domain.ChromePdfOptions;
import te.htmltopdf.domain.PdfFile;

class HtmlToPdfFileConverter {

    //TODO: Remove this hard coding
    public static final String INPUT_FILE_PATH = "C:\\Users\\Todd\\Desktop\\big.html";

    private ChromePdfFileGenerator chromePdfGenerator = new ChromePdfFileGenerator();

    // Works perfectly with HTML we haven't modified
    // tl;dr having a method that simply takes a string of HTML is non-trivial
    //TODO: Add a String equivalent of this once the rest of the logic is flushed out
    PdfFile convert(File htmlContents) {
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

        return chromePdfGenerator.generate(options, htmlContents);
    }

}