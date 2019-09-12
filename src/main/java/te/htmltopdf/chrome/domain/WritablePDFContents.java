package te.htmltopdf.chrome.domain;

import io.vavr.control.Try;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import te.htmltopdf.WritablePDF;
import te.htmltopdf.ChromeHtmlToPdfConverter;

/**
 * Represents a PDF file in memory that can be written to an {@link OutputStream}.  These are
 * returned by {@link ChromeHtmlToPdfConverter} since the underlying Chrome
 * process returns PDF content as a {@link String}.
 */
public class WritablePDFContents implements WritablePDF {

    private final String contentsInMemory;

    public WritablePDFContents(String contentsInMemory) {
        this.contentsInMemory = contentsInMemory;
    }

    /**
     * Writes the underlying PDF file to a given {@link OutputStream}.
     */
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        Try.of(() -> outputStream)
            .andThenTry(() -> outputStream.write(Base64.getDecoder().decode(contentsInMemory)))
            .andFinallyTry(outputStream::close);
    }

    public String getContentsInMemory() {
        return contentsInMemory;
    }

}
