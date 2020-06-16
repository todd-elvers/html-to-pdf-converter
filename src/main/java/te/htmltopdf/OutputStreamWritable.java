package te.htmltopdf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @see te.htmltopdf.chrome.domain.InMemoryPDF
 * @see te.htmltopdf.wkhtmltopdf.domain.OnDiskPDF
 */
public interface OutputStreamWritable {
    void write(OutputStream outputStream) throws IOException;
}
