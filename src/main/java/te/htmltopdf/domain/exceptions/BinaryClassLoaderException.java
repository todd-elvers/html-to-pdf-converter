package te.htmltopdf.domain.exceptions;

public class BinaryClassLoaderException extends RuntimeException {

    public BinaryClassLoaderException() {
        super("Could not find wkhtmltopdf binary via the current thread's context class loader.");
    }

}
