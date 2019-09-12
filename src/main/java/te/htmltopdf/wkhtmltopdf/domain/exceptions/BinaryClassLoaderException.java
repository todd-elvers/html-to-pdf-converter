package te.htmltopdf.wkhtmltopdf.domain.exceptions;

public class BinaryClassLoaderException extends RuntimeException {

    public BinaryClassLoaderException() {
        super("Could not find wkhtmltopdf binary via the current thread's context class loader.");
    }

}
