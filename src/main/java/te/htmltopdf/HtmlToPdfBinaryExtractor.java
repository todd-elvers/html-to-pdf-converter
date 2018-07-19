package te.htmltopdf;

import io.vavr.control.Try;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.domain.exceptions.BinaryExtractionException;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * Extracts a wkhtmltopdf binary out of this JAR to a temporary directory.
 */
public class HtmlToPdfBinaryExtractor {
    private static final Logger log = LoggerFactory.getLogger(HtmlToPdfBinaryExtractor.class);

    public File extract() {
        log.info("Extracting wkhtmltopdf binary for this OS.");
        return writeBinaryToTempDir(determineBinaryFilename());
    }

    protected String determineBinaryFilename() {
        if (IS_OS_MAC) {
            return "wkhtmltopdf_mac";
        } else if (IS_OS_WINDOWS) {
            return "wkhtmltopdf_win.exe";
        } else {
            return "wkhtmltopdf_linux";
        }
    }

    protected File writeBinaryToTempDir(String filename) {
        InputStream executableFileStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        File executableFile = createNewTempFile(filename);

        Try.run(() -> FileUtils
                .copyInputStreamToFile(executableFileStream, executableFile))
                .getOrElseThrow(BinaryExtractionException::new);

        return executableFile;
    }

    protected File createNewTempFile(String filename) {
        return Try.of(() -> Files.createTempDirectory("html-to-pdf-converter"))
                .mapTry(Path::toFile)
                .mapTry(tempDir -> new File(tempDir, filename))
                .getOrElseThrow(ex -> new RuntimeException("Failed to create a temp. directory for the wkhtmltopdf binary.", ex));
    }
}
