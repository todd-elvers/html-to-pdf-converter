package te.htmltopdf;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;

/**
 * Resolves the location of the wkhtmltopdf binary to use for converting.
 *
 * <p>By default, this will extract the binary for your OS to a temp. directory.
 * <br/>
 * Override this by setting the environment variable <code>WKHTMLTOPDF_BINARY</code> to the path of a wkhtmltopdf binary
 * you wish to use instead.
 */
public class HtmlToPdfBinaryResolver {
    private static final Logger log = LoggerFactory.getLogger(HtmlToPdfBinaryResolver.class);
    private static final String BINARY_SYSTEM_VARIABLE = "WKHTMLTOPDF_BINARY";
    protected HtmlToPdfBinaryExtractor htmlToPdfBinaryExtractor;

    public HtmlToPdfBinaryResolver() {
        this.htmlToPdfBinaryExtractor = new HtmlToPdfBinaryExtractor();
    }

    /**
     * @return an executable wkhtmltopdf binary
     */
    public File resolve() {
        return asExecutable(resolveBinaryFile());
    }

    protected File resolveBinaryFile() {
        if (isEnvironmentVariableSet()) {
            File binary = resolveBinaryFromEnvironmentVariable();
            if (binary.exists()) {
                log.info("Using binary from environment variable @ {}.", binary.getAbsolutePath());
                return binary;
            } else {
                log.error("Binary specified in environment variable could not be found.", binary.getAbsolutePath());
            }
        }

        return htmlToPdfBinaryExtractor.extract();
    }

    protected File resolveBinaryFromEnvironmentVariable() {
        return new File(System.getenv(BINARY_SYSTEM_VARIABLE));
    }

    protected boolean isEnvironmentVariableSet() {
        log.debug("{} system variable is set to {}.", BINARY_SYSTEM_VARIABLE, System.getenv(BINARY_SYSTEM_VARIABLE));
        return StringUtils.isNotEmpty(
                System.getenv(BINARY_SYSTEM_VARIABLE)
        );
    }

    protected File asExecutable(File binary) {
        if (!Files.isExecutable(binary.toPath())) {
            if (!binary.setExecutable(true)) {
                throw new RuntimeException("Failed to make '" + binary.getName() + "' executable.");
            }
        }

        return binary;
    }

}
