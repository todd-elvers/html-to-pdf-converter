package te.htmltopdf.chrome;

import io.vavr.Function0;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import te.htmltopdf.ToPdfConverter;
import te.htmltopdf.chrome.domain.InMemoryPDF;
import te.htmltopdf.chrome.domain.OptionsForPDF;
import te.htmltopdf.wkhtmltopdf.TempFileGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Function;

public class ChromeHtmlToPdfConverter implements ToPdfConverter<InMemoryPDF> {
    protected final TempFileGenerator tempFileGenerator = new TempFileGenerator();
    protected final Charset charset;
    private final ChromePdfFileGenerator chromePdfGenerator;

    public ChromeHtmlToPdfConverter() {
        this.charset = Charset.defaultCharset();
        this.chromePdfGenerator = new ChromePdfFileGenerator();
    }

    public ChromeHtmlToPdfConverter(Charset charset, ChromePdfFileGenerator chromePdfGenerator) {
        this.charset = charset;
        this.chromePdfGenerator = chromePdfGenerator;
    }

    public InMemoryPDF tryToConvert(InputStream inputStream) throws IOException {
        String html = IOUtils.toString(inputStream, charset);
        return tryToConvert(html);
    }

    public InMemoryPDF tryToConvert(String html) throws IOException {
        File tempFile = tempFileGenerator.generateTempForPDF();

        FileUtils.writeStringToFile(
                tempFile,
                html,
                charset,
                false
        );

        return tryToConvert(tempFile, Function.identity());
    }

    public InMemoryPDF tryToConvert(File html) {
        return tryToConvert(html, Function.identity());
    }

    // TODO: Declare throws here?
    // TODO: Test options configurer
    public InMemoryPDF tryToConvert(
            File html,
            Function<OptionsForPDF.Builder, OptionsForPDF.Builder> customizeWithExtraOptions
    ) {
        return Function0.of(this::defaultChromePdfOptions)
                .andThen(customizeWithExtraOptions)
                .andThen(OptionsForPDF.Builder::build)
                .andThen(options -> chromePdfGenerator.generate(html, options))
                .get();
    }

    /**
     * @return an instance of {@link OptionsForPDF.Builder} populated with some
     * default PDF generation options for Chrome
     */
    public OptionsForPDF.Builder defaultChromePdfOptions() {
        return new OptionsForPDF.Builder()
                .setLandscape(false)
                .setDisplayHeaderFooter(false)
                .setPrintBackground(true)
                .setScale(1d)
                .setPaperWidth(8.27d)   // A4 paper
                .setPaperHeight(11.7d)  // A4 paper
                .setMarginTop(0d)
                .setMarginBottom(0d)
                .setMarginLeft(0d)
                .setMarginRight(0d)
                .setPageRanges(" ")
                .setIgnoreInvalidPageRanges(false)
                .setHeaderTemplate(" ")
                .setFooterTemplate(" ")
                .setPreferCSSPageSize(false);
    }
}