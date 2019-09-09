package te.htmltopdf.wkhtmltopdf;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.domain.exceptions.MakingFileExecutableException;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Resolves the location of the wkhtmltopdf binary to use for converting.
 *
 * <p>The OS-specific binary for your operating system will be lazily cached to a temporary file
 * upon the first call to {@link #resolve()}.  All subsequent calls will utilize the cached binary.
 * <p>
 * <br/>
 * <p>
 * To disable the caching process stated above simply set the environment variable (or system
 * property) <code>WKHTMLTOPDF_BINARY</code> to the path of the wkhtmltopdf binary on your system.
 */
public class WkHtmlToPdfBinaryResolver {
    private static final Logger log = LoggerFactory.getLogger(WkHtmlToPdfBinaryResolver.class);
    public static final String BINARY_ENV_VAR_NAME = "WKHTMLTOPDF_BINARY";

    protected final WkHtmlToPdfBinaryExtractor binaryExtractor;


    public WkHtmlToPdfBinaryResolver(WkHtmlToPdfBinaryExtractor binaryExtractor) {
        this.binaryExtractor = binaryExtractor;
    }

    public WkHtmlToPdfBinaryResolver() {
        this.binaryExtractor = new WkHtmlToPdfBinaryExtractor();
    }

    /**
     * @return a reference to the executable wkhtmltopdf binary cached in the temporary file
     * or specified in the environment variable, or system property, {@link #BINARY_ENV_VAR_NAME}.
     */
    public File resolve() {
        return checkForCustomBinaryPath(System.getenv(), System.getProperties())
                .map(File::new)
                .orElse(() -> Option.of(binaryExtractor.extract()))
                .map(this::makeExecutable)
                .get();
    }

    protected Option<String> checkForCustomBinaryPath(Map<String, String> envVariables, Properties systemProperties) {
        String envVariable = envVariables.get(BINARY_ENV_VAR_NAME);
        String sysProperty = systemProperties.getProperty(BINARY_ENV_VAR_NAME);

        if (isNotBlank(envVariable)) {
            log.info("Custom binary path found @ {}", envVariable);
            return Option.of(envVariable);
        }

        if (isNotBlank(sysProperty)) {
            log.info("Custom binary path found @ {}", sysProperty);
            return Option.of(sysProperty);
        }

        return Option.none();
    }

    protected File makeExecutable(File binary) {
        if (!Files.isExecutable(binary.toPath())) {
            boolean isExecutableNow = Try.of(() -> binary.setExecutable(true))
                    .getOrElseThrow(securityException ->
                            new MakingFileExecutableException(binary, securityException)
                    );

            if (!isExecutableNow) {
                throw new MakingFileExecutableException(binary);
            }
        }

        return binary;
    }

}
