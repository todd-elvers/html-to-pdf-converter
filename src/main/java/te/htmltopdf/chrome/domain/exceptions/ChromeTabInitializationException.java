package te.htmltopdf.chrome.domain.exceptions;

public class ChromeTabInitializationException extends RuntimeException {

    public ChromeTabInitializationException(Throwable cause) {
        super("Error occurred while initializing a connection to the running Chrome service.", cause);
    }
}
