package te.htmltopdf.wkhtmltopdf;

import io.vavr.control.Try;
import te.htmltopdf.wkhtmltopdf.domain.exceptions.TempFileCreationException;

import java.io.File;
import java.util.function.Function;

public class TempFileGenerator {

    public File generateTempForPDF() throws TempFileCreationException {
        return generate("pdf-temp-", "pdf", TempFileCreationException::forPDF);
    }

    public File generateTempForExecutable(String prefix, String suffix) throws TempFileCreationException {
        return generate(prefix, suffix, TempFileCreationException::forExecutable);
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
