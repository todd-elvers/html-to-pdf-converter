package te.htmltopdf.domain;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Represents a PDF file on disk.
 *
 * <p>Exists to abstract away the process of writing the PDF file to an output stream and deleting it from disk afterwords.
 * <p>The {@link Closeable} interface is implemented here so we can utilize try-with-resources.
 */
public class PdfFile implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(PdfFile.class);

    private final File fileOnDisk;

    public PdfFile(File fileOnDisk) {
        log.info("PDF generated and written to {}", fileOnDisk.getAbsolutePath());
        this.fileOnDisk = fileOnDisk;
    }

    /**
     * Writes the underlying PDF file to a given {@link OutputStream}.
     */
    public void writeToOutputStream(OutputStream outputStream) throws IOException {
        try (InputStream inputStream = FileUtils.openInputStream(fileOnDisk)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    public File getFileOnDisk() {
        return fileOnDisk;
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(fileOnDisk);
        log.info("PDF file deleted from disk.");
    }

}
