package te.htmltopdf.wkhtmltopdf;

import io.vavr.control.Try;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;

import static java.nio.charset.Charset.defaultCharset;

/**
 * Streams a string of HTML into our wkhtmltopdf command.
 */
public class HtmlPumpStreamHandler extends PumpStreamHandler implements Closeable {
    private static final Logger log = LoggerFactory.getLogger(HtmlPumpStreamHandler.class);

    private final ByteArrayOutputStream streamOfOutputTextFromBinary;
    private final ByteArrayInputStream streamOfHtmlInFromMemory;

    /**
     * Creates a HtmlStreamHandler capable of streaming HTML in from memory so that it can be piped to the wkhtmltopdf binary
     * without the need to write it to disk first.
     */
    public HtmlPumpStreamHandler(String html) {
        this(
                new ByteArrayInputStream(html.getBytes(defaultCharset())),
                new ByteArrayOutputStream()
        );
    }

    /**
     * Creates a HtmlStreamHandler capable of streaming HTML in from memory so that it can be piped to the wkhtmltopdf binary
     * without the need to write it to disk first.
     */
    public HtmlPumpStreamHandler(ByteArrayInputStream streamOfHtmlInFromMemory, ByteArrayOutputStream streamOfOutputTextFromBinary) {
        // Even though the method signature is `super(stdout, stderr, stdin)`, after some extensive testing I found these are misnamed.
        // The method signature should really be `super(stderr, stdout, stdin)`, but stderr can be ignored because everything is written to stdout.
        super(null, streamOfOutputTextFromBinary, streamOfHtmlInFromMemory);

        this.streamOfOutputTextFromBinary = streamOfOutputTextFromBinary;
        this.streamOfHtmlInFromMemory = streamOfHtmlInFromMemory;
    }

    public String getOutputTextFromBinary() {
        return streamOfOutputTextFromBinary.toString().trim();
    }

    @Override
    public void close() {
        log.debug("wkhtmltopdf stdout:\n{}", getOutputTextFromBinary());

        Try.run(this::stop)
                .andFinally(() -> {
                    closeQuietly(streamOfOutputTextFromBinary);
                    closeQuietly(streamOfHtmlInFromMemory);
                });
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) closeable.close();
        } catch (final IOException ioe) {
            // Ignore
        }
    }
}