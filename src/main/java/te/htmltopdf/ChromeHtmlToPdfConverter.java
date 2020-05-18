package te.htmltopdf;

import io.vavr.Function0;
import java.io.File;
import java.util.function.Function;
import te.htmltopdf.chrome.ChromePdfFileGenerator;
import te.htmltopdf.chrome.domain.InMemoryPDF;
import te.htmltopdf.chrome.domain.OptionsForPDF;
import te.htmltopdf.chrome.domain.OptionsForPDF.Builder;

public class ChromeHtmlToPdfConverter {

    private final ChromePdfFileGenerator chromePdfGenerator;

    public ChromeHtmlToPdfConverter() {
        this.chromePdfGenerator = new ChromePdfFileGenerator();
    }

    public ChromeHtmlToPdfConverter(ChromePdfFileGenerator chromePdfGenerator) {
        this.chromePdfGenerator = chromePdfGenerator;
    }

    //TODO: Add a String equivalent of this once the rest of the logic is flushed out
    public InMemoryPDF tryToConvert(File htmlContents) {
        return tryToConvert(htmlContents, Function.identity());
    }


    // TODO: Declare throws here?
    // TODO: Test options configurer
    public InMemoryPDF tryToConvert(
        File htmlContents,
        Function<OptionsForPDF.Builder, OptionsForPDF.Builder> customizeWithExtraOptions
    ) {
        return Function0.of(this::defaultChromePdfOptions)
            .andThen(customizeWithExtraOptions)
            .andThen(Builder::build)
            .andThen(options -> chromePdfGenerator.generate(htmlContents, options))
            .get();
    }

    protected OptionsForPDF.Builder defaultChromePdfOptions() {
        return new OptionsForPDF.Builder()
            .setDisplayHeaderFooter(true)
            .setPrintBackground(true)
            .setScale(1d)
            .setPaperWidth(8.5d)
            .setPaperHeight(11d)
            .setMarginTop(0.4d)
            .setMarginBottom(0.4d)
            .setMarginLeft(0.4d)
            .setMarginRight(0.4d)
            .setPageRanges(" ")
            .setIgnoreInvalidPageRanges(false)
            .setHeaderTemplate(" ")
            .setFooterTemplate(" ")
            .setPreferCSSPageSize(false);
    }
}