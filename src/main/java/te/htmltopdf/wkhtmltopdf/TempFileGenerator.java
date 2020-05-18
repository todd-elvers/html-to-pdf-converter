package te.htmltopdf.wkhtmltopdf;

import io.vavr.control.Try;
import te.htmltopdf.wkhtmltopdf.domain.exceptions.TempFileCreationException;

import java.io.File;
import java.util.function.Function;

/**
 * Responsible for generating temporary files in the temporary directory.
 */
public class TempFileGenerator {

    public File generateTempOutputFile() throws TempFileCreationException {
        return generate("pdf-temp-", "pdf", TempFileCreationException::forBinaryExtraction);
    }

    public File generateTempBinaryFile(String prefix, String suffix) throws TempFileCreationException {
        return generate(prefix, suffix, TempFileCreationException::forOutputFile);
    }

    protected File generate(
            String prefix,
            String suffix,
            Function<Throwable, TempFileCreationException> exceptionTransformer
    ) throws TempFileCreationException {
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
