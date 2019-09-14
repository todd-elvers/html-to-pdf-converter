package te.htmltopdf;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents a PDF that can be written to an {@link OutputStream}.
 */
public interface WritablePDF {

    void write(OutputStream outputStream) throws IOException;

}
