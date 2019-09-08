package te.htmltopdf.wkhtmltopdf;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

import io.vavr.Function0;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.control.Option;
import io.vavr.control.Try;
import te.htmltopdf.TempFileGenerator;
import te.htmltopdf.domain.exceptions.BinaryClassLoaderException;
import te.htmltopdf.domain.exceptions.BinaryExtractionException;

import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * Extracts a wkhtmltopdf binary out of this JAR to a temporary directory.
 */
@SuppressWarnings("WeakerAccess")
public class WkHtmlToPdfBinaryExtractor {
    private static final Logger log = LoggerFactory.getLogger(WkHtmlToPdfBinaryExtractor.class);

    protected final TempFileGenerator tempFileGenerator;

    public WkHtmlToPdfBinaryExtractor() {
        this(new TempFileGenerator());
    }

    public WkHtmlToPdfBinaryExtractor(TempFileGenerator tempFileGenerator) {
        this.tempFileGenerator = tempFileGenerator;
    }

    /**
     * Extracts the OS-specific wkhtmltopdf binary from our JAR and streams it to a temporary file.
     *
     * @return a reference to the OS-specific wkhtmltopdf binary on disk
     */
    public File extract() {
        log.info("Extracting wkhtmltopdf binary for this OS.");

        return Function0.of(this::prepareForExtraction)
                .andThen(this::createEmptyTempFile)
                .andThen(this::openStreamToBinaryInJAR)
                .andThen(this::streamBinaryContentsToTempFile)
                .apply();
    }

    protected Tuple3<String, File, InputStream> prepareForExtraction() {
        return Tuple.of(determineBinaryFilename(), null, null);
    }

    //TODO: Add validation to this, we want to eagerly fail when the current OS is not supported
    protected String determineBinaryFilename() {
        if (IS_OS_MAC) return "wkhtmltopdf_mac";
        if (IS_OS_WINDOWS) return "wkhtmltopdf_win.exe";
        return "wkhtmltopdf_linux";
    }

    protected Tuple3<String, File, InputStream> createEmptyTempFile(Tuple3<String, File, InputStream> extraction) {
        return extraction.update2(
                tempFileGenerator.generateTempBinaryFile(removeExtension(extraction._1), getExtension(extraction._1))
        );
    }

    /**
     * Opens a stream to one of the binaries packaged in this library.
     *
     * @throws BinaryClassLoaderException when the Thread's context classloader is null or when
     *                                    fetching the resource as a stream returns null
     */
    protected Tuple3<String, File, InputStream> openStreamToBinaryInJAR(Tuple3<String, File, InputStream> extraction) {
        return extraction.update3(
                Option
                        .of(Thread.currentThread().getContextClassLoader())
                        .flatMap(classloader -> Option.of(classloader.getResourceAsStream(extraction._1)))
                        .getOrElseThrow(BinaryClassLoaderException::new)
        );
    }

    protected File streamBinaryContentsToTempFile(Tuple3<String, File, InputStream> extraction) {
        Try.run(() -> FileUtils.copyInputStreamToFile(extraction._3, extraction._2))
                .getOrElseThrow(BinaryExtractionException::new);

        return extraction._2;
    }
}
