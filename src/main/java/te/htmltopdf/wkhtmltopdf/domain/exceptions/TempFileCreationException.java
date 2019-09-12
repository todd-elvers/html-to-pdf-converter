package te.htmltopdf.wkhtmltopdf.domain.exceptions;

import static org.apache.commons.lang3.SystemUtils.getJavaIoTmpDir;

public class TempFileCreationException extends RuntimeException {

    public TempFileCreationException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static TempFileCreationException forBinaryExtraction(Throwable throwable) {
        return new TempFileCreationException(
                "Failed to create a temporary file to extract the wkhtmltopdf binary to.\n" +
                        messageSuffix(),
                throwable
        );
    }

    public static TempFileCreationException forOutputFile(Throwable throwable) {
        return new TempFileCreationException(
                "Failed to create a temporary file to write the resulting PDF to.\n" +
                        messageSuffix(),
                throwable
        );
    }

    private static String messageSuffix() {
        return "Ensure you have the correct permissions to create files in:\n" +
                getJavaIoTmpDir().getAbsolutePath();
    }
}
