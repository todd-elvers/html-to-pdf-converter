package te.htmltopdf;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.vavr.control.Try;
import te.htmltopdf.domain.exceptions.BinaryClassLoaderException;
import te.htmltopdf.domain.exceptions.BinaryExtractionException;
import te.htmltopdf.domain.exceptions.TempFileCreationException;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;
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
        if (IS_OS_MAC) return "wkhtmltopdf_mac";
        if (IS_OS_WINDOWS) return "wkhtmltopdf_win.exe";
        return "wkhtmltopdf_linux";
    }

    protected File writeBinaryToTempDir(String filename) {
        File executableFile = createNewTempFile(filename);
        InputStream executableContentsInJAR = getStreamToExecutableInJAR(filename);

        Try.run(() -> FileUtils
                .copyInputStreamToFile(executableContentsInJAR, executableFile))
                .getOrElseThrow(BinaryExtractionException::new);

        return executableFile;
    }

    protected InputStream getStreamToExecutableInJAR(String filename) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) throw new BinaryClassLoaderException();

        InputStream executableFileStream = classLoader.getResourceAsStream(filename);
        if (executableFileStream == null) throw new BinaryClassLoaderException();

        return executableFileStream;
    }

    protected File createNewTempFile(String filename) {
        return Try.of(() -> Files.createTempFile(removeExtension(filename), getExtension(filename)))
                .mapTry(Path::toFile)
                .getOrElseThrow(TempFileCreationException::new);
    }
}
