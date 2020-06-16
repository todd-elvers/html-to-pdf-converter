package te.htmltopdf.wkhtmltopdf.domain.exceptions;

public class UnsupportedOperatingSystemException extends RuntimeException {

    public UnsupportedOperatingSystemException() {
        super("The operating system either could not be correctly determined or is not supported by WkHTMLtoPDF.");
    }

}
