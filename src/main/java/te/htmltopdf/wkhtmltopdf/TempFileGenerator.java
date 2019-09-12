package te.htmltopdf.wkhtmltopdf;

import java.io.File;
import java.util.function.Function;

import io.vavr.control.Try;
import te.htmltopdf.wkhtmltopdf.domain.exceptions.TempFileCreationException;

/**
 * Responsible for generating temporary files in the temporary directory.
 */
public class TempFileGenerator {

    public File generateTempOutputFile() {
        return generate("pdf-temp-", "pdf", TempFileCreationException::forBinaryExtraction);
    }

    public File generateTempBinaryFile(String prefix, String suffix) {
        return generate(prefix, suffix, TempFileCreationException::forOutputFile);
    }

    protected File generate(String prefix, String suffix, Function<Throwable, TempFileCreationException> exceptionTransformer) {
        return Try
                .of(() -> File.createTempFile(prefix, prependPeriodIfMissing(suffix)))
                .getOrElseThrow(exceptionTransformer);
    }

    private String prependPeriodIfMissing(String fileSuffix) {
        return fileSuffix.startsWith(".")
                ? fileSuffix
                : "." + fileSuffix;
    }
}
