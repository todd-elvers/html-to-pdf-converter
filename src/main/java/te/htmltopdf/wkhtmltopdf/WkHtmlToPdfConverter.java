package te.htmltopdf.wkhtmltopdf;

import io.vavr.control.Try;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import te.htmltopdf.ToPdfConverter;
import te.htmltopdf.wkhtmltopdf.domain.OnDiskPDF;
import te.htmltopdf.wkhtmltopdf.domain.exceptions.HtmlToPdfConversionException;

/**
 * Converts HTML documents to PDF documents.
 *
 * <p>Under the hood this uses Apache Exec, a synchronization-block, and a timeout to ensure
 * a thread-safe, non-locking interaction between this class and the command line.
 *
 * @see <a href="https://wkhtmltopdf.org/">WkHTMLtoPDF site</href>
 */
@ThreadSafe
public class WkHtmlToPdfConverter implements ToPdfConverter<OnDiskPDF> {
    public static final int COMMAND_TIMEOUT_IN_MILLIS = 15_000;
    public static final String EXPECT_FILE_AS_STREAM_FROM_STD_IN = "-";
    private static final Object LOCK = new Object[0];

    // v2.0.0 features list:
    //TODO: Get tests for newest conversion features passing
    //TODO: Check response code from wkhtmltopdf and output error message from console
    //TODO: Correct documentation on existing methods
    //TODO: Add documentation to new methods

    @GuardedBy("WkHtmlToPdfConverter$LOCK")
    protected final File wkHtmlToPdfBinary;
    protected final Charset charset;
    protected final TempFileGenerator tempFileGenerator;

    public WkHtmlToPdfConverter() {
        this(Charset.defaultCharset(), new WkHtmlToPdfBinaryResolver().resolve(), new TempFileGenerator());
    }

    public WkHtmlToPdfConverter(Charset charset, File wkHtmlToPdfBinary, TempFileGenerator tempFileGenerator) {
        this.charset = charset;
        this.wkHtmlToPdfBinary = wkHtmlToPdfBinary;
        this.tempFileGenerator = tempFileGenerator;
    }

    public OnDiskPDF tryToConvert(File file) throws IOException {
        return tryToConvert(
                FileUtils.readFileToString(file, charset),
                tempFileGenerator.generateTempForPDF()
        );
    }

    public OnDiskPDF tryToConvert(InputStream inputStream) throws IOException {
        return tryToConvert(
                String.join("\n", IOUtils.readLines(inputStream, charset)),
                tempFileGenerator.generateTempForPDF()
        );
    }

    /**
     * Generates a PDF from the provided HTML and writes it to a temporary file.
     *
     * <p>Instead of writing the HTML to file first, it is piped directly from memory into the
     * WkHTMLtoPDF binary, saving us some disk IO.  For more information, see the {@link
     * HtmlPumpStreamHandler}.
     *
     * <p>The resulting {@link OnDiskPDF} implements {@link java.io.Closeable} to simplify deleting
     * the file on disk after we're done with it.
     *
     * <p>Usage example:
     * <pre>
     *   try (PdfFile pdfFile = htmlToPdfFileConverter.convert(html)) {
     *       ...
     *   } catch (IOException exception) {
     *       log.error("An error occurred in the HTML -> PDF conversion.", exception);
     *       throw exception;
     *   }
     * </pre>
     *
     * @return a {@link OnDiskPDF} containing a reference to the PDF file that was created
     */
    public OnDiskPDF tryToConvert(String html) throws IOException {
        return tryToConvert(
                html,
                tempFileGenerator.generateTempForPDF()
        );
    }

    /**
     * {@link #tryToConvert(String)} but also accepts a function to allow for customizing the
     * call to the `wkhtmltopdf` binary.
     *
     * @see #tryToConvert(String)
     * @see #tryToConvert(String, File, Function)
     */
    public OnDiskPDF tryToConvert(
            String html,
            Function<CommandLine, CommandLine> commandCustomizer
    ) throws HtmlToPdfConversionException {
        return tryToConvert(
                html,
                tempFileGenerator.generateTempForPDF(), commandCustomizer
        );
    }

    /**
     * {@link #tryToConvert(String)} but also accepts the file to write the PDF to.
     *
     * @see #tryToConvert(String)
     * @see #tryToConvert(String, File, Function)
     */
    public OnDiskPDF tryToConvert(String html, File outputFile) throws HtmlToPdfConversionException {
        return tryToConvert(
                html,
                outputFile,
                Function.identity()
        );
    }

    /**
     * {@link #tryToConvert(String, File)} but also accepts a function to allow for customizing the
     * call to the `wkhtmltopdf` binary.
     *
     * @see #tryToConvert(String)
     * @see #tryToConvert(String, File)
     */
    public OnDiskPDF tryToConvert(
            String html,
            File outputFile,
            Function<CommandLine, CommandLine> commandCustomizer
    ) throws HtmlToPdfConversionException {
        synchronized (LOCK) {
            CommandLine command = commandCustomizer.apply(
                    createConversionCommand(outputFile)
            );

            return tryToExecuteCommand(command, html, outputFile);
        }
    }

    protected CommandLine createConversionCommand(File outputFile) {
        return new CommandLine(wkHtmlToPdfBinary)
                .addArgument(EXPECT_FILE_AS_STREAM_FROM_STD_IN)
                .addArgument(outputFile.getAbsolutePath());
    }

    protected OnDiskPDF tryToExecuteCommand(CommandLine conversionCommand, String html, File outputFile) throws HtmlToPdfConversionException {
        DefaultExecutor executor = new DefaultExecutor();

        Try.withResources(() -> new HtmlPumpStreamHandler(html))
                .of(htmlStreamHandler -> executeCommand(executor, htmlStreamHandler, conversionCommand))
                .getOrElseThrow(ex -> new HtmlToPdfConversionException(executor, ex));

        return new OnDiskPDF(outputFile);
    }

    protected int executeCommand(
            Executor executor,
            PumpStreamHandler streamHandler,
            CommandLine command
    ) throws IOException {
        executor.setWatchdog(createCommandLineWatchdog());
        executor.setStreamHandler(streamHandler);
        return executor.execute(command);
    }

    protected ExecuteWatchdog createCommandLineWatchdog() {
        return new ExecuteWatchdog(COMMAND_TIMEOUT_IN_MILLIS);
    }
}
