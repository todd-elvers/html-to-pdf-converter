package te.htmltopdf.chrome.domain;

import io.vavr.control.Try;
import te.htmltopdf.chrome.ChromeHtmlToPdfConverter;
import te.htmltopdf.OutputStreamWritable;

import java.io.OutputStream;
import java.util.Base64;

/**
 * Represents a PDF file in memory that can be written to an {@link OutputStream}.  These are
 * returned by {@link ChromeHtmlToPdfConverter} since the underlying Chrome
 * process returns PDF content as a {@link String}.
 */
public class InMemoryPDF implements OutputStreamWritable {

    private final String contents;

    public InMemoryPDF(String contents) {
        this.contents = contents;
    }

    /**
     * Writes the underlying PDF file to a given {@link OutputStream}.
     */
    public void write(OutputStream outputStream) {
        Try.of(() -> outputStream)
                .andThenTry(() -> outputStream.write(Base64.getDecoder().decode(contents)))
                .andFinallyTry(outputStream::close);
    }

    public String getContents() {
        return contents;
    }

}
