package te.htmltopdf.wkhtmltopdf.domain;

import io.vavr.control.Try;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.WkHtmlToPdfConverter;
import te.htmltopdf.WritablePDF;

/**
 * Represents a PDF file on disk that can be written to an {@link OutputStream}.  These are returned
 * from {@link WkHtmlToPdfConverter} since the underlying binary must
 * output to a file.
 *
 * <p>
 * {@link #close()} will delete the underlying temporary file from disk so only call it after you
 * have written this file to the desired {@link OutputStream}.
 */
public class WritablePDFFile implements WritablePDF, Closeable {

    private static final Logger log = LoggerFactory.getLogger(WritablePDFFile.class);

    private final File temporaryFile;

    public WritablePDFFile(File temporaryFile) {
        log.info("PDF generated and written to {}", temporaryFile.getAbsolutePath());
        this.temporaryFile = temporaryFile;
    }

    /**
     * Writes the underlying PDF file to a given {@link OutputStream}.
     */
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        Try.withResources(() -> FileUtils.openInputStream(temporaryFile))
            .of(inputStream -> IOUtils.copy(inputStream, outputStream))
            .get();
    }

    public File getTemporaryFile() {
        return temporaryFile;
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(temporaryFile);
        log.info("PDF file deleted from disk.");
    }

}
