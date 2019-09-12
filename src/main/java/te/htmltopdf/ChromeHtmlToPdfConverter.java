package te.htmltopdf;

import java.io.File;
import te.htmltopdf.chrome.ChromePdfFileGenerator;
import te.htmltopdf.chrome.domain.ChromePdfOptions;
import te.htmltopdf.chrome.domain.WritablePDFContents;

public class ChromeHtmlToPdfConverter {

    private final ChromePdfFileGenerator chromePdfGenerator;

    public ChromeHtmlToPdfConverter() {
        this.chromePdfGenerator = new ChromePdfFileGenerator();
    }

    public ChromeHtmlToPdfConverter(ChromePdfFileGenerator chromePdfGenerator) {
        this.chromePdfGenerator = chromePdfGenerator;
    }

    //TODO: Add a String equivalent of this once the rest of the logic is flushed out
    public WritablePDFContents tryToConvert(File htmlContents) {
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