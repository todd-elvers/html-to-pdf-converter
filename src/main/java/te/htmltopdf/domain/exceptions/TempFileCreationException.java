package te.htmltopdf.domain.exceptions;

public class TempFileCreationException extends RuntimeException {

    public TempFileCreationException(Throwable throwable) {
        super("Failed to create a temp. directory for the wkhtmltopdf binary.", throwable);
    }

}
