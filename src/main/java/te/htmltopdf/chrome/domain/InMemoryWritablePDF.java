package te.htmltopdf.chrome.domain;

import io.vavr.control.Try;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import te.htmltopdf.ChromeHtmlToPdfConverter;
import te.htmltopdf.WritablePDF;

/**
 * Represents a PDF file in memory that can be written to an {@link OutputStream}.  These are
 * returned by {@link ChromeHtmlToPdfConverter} since the underlying Chrome
 * process returns PDF content as a {@link String}.
 */
public class InMemoryWritablePDF implements WritablePDF {

    private final String contentsInMemory;

    public InMemoryWritablePDF(String contentsInMemory) {
        this.contentsInMemory = contentsInMemory;
    }

    /**
     * Writes the underlying PDF file to a given {@link OutputStream}.
     */
    public void write(OutputStream outputStream) throws IOException {
        Try.of(() -> outputStream)
            .andThenTry(() -> outputStream.write(Base64.getDecoder().decode(contentsInMemory)))
            .andFinallyTry(outputStream::close);
    }

    public String getContentsInMemory() {
        return contentsInMemory;
    }

}
