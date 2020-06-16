package te.htmltopdf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a class capable of taking some HTML and converting it to PDF, in
 * the form of an {@link OutputStreamWritable} instance.
 *
 * @param <T> the type this PDF converter generates
 * @see te.htmltopdf.chrome.ChromeHtmlToPdfConverter
 * @see te.htmltopdf.wkhtmltopdf.WkHtmlToPdfConverter
 */
public interface ToPdfConverter<T extends OutputStreamWritable> {
    T tryToConvert(String html) throws IOException;

    T tryToConvert(File htmlFile) throws IOException;

    T tryToConvert(InputStream htmlInputStream) throws IOException;
}
