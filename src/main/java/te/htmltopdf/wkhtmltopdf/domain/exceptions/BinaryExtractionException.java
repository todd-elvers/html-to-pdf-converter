package te.htmltopdf.wkhtmltopdf.domain.exceptions;

public class BinaryExtractionException extends RuntimeException {
    public BinaryExtractionException(Throwable throwable) {
        super("Failed to extract wkhtmltopdf binary to a temp. file.", throwable);
    }
}
