package te.htmltopdf;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.vavr.Function0;
import io.vavr.Tuple3;
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

        return Function0
                .of(this::prepareForBinaryFileExtraction)
                .andThen(this::createNewTempFile)
                .andThen(this::openStreamOfBinaryContents)
                .andThen(this::writeBinaryContentsToTempFile)
                .apply();
    }

    protected Tuple3<String, File, InputStream> prepareForBinaryFileExtraction() {
        return new Tuple3<>(determineBinaryFilename(), null, null);
    }

    protected Tuple3<String, File, InputStream> openStreamOfBinaryContents(Tuple3<String, File, InputStream> extraction) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) throw new BinaryClassLoaderException();

        InputStream executableFileStream = classLoader.getResourceAsStream(extraction._1);
        if (executableFileStream == null) throw new BinaryClassLoaderException();

        return extraction.update3(executableFileStream);
    }

    protected String determineBinaryFilename() {
        if (IS_OS_MAC) return "wkhtmltopdf_mac";
        if (IS_OS_WINDOWS) return "wkhtmltopdf_win.exe";
        return "wkhtmltopdf_linux";
    }

    protected Tuple3<String, File, InputStream> createNewTempFile(Tuple3<String, File, InputStream> extraction) {
        return extraction.update2(
                Try.of(() -> Files.createTempFile(removeExtension(extraction._1), getExtension(extraction._1)))
                        .mapTry(Path::toFile)
                        .getOrElseThrow(TempFileCreationException::new)
        );
    }

    protected File writeBinaryContentsToTempFile(Tuple3<String, File, InputStream> extraction) {
        Try.run(() -> FileUtils.copyInputStreamToFile(extraction._3, extraction._2))
                .getOrElseThrow(BinaryExtractionException::new);

        return extraction._2;
    }
}
