package te.htmltopdf.chrome.domain.exceptions;

public class ChromeTabExecutionException extends RuntimeException {
    public static final String MESSAGE = "Error occurred while Chrome tab was open, causing it to crash.";

    public ChromeTabExecutionException(Throwable cause) {
        super(MESSAGE, cause);
    }

}
