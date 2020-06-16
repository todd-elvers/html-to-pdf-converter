package te.htmltopdf;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamWritable {
    void write(OutputStream outputStream) throws IOException;
}
