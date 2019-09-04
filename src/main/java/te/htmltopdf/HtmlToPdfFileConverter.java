package te.htmltopdf;

import io.vavr.control.Try;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.exec.*;
import te.htmltopdf.domain.PdfFile;
import te.htmltopdf.domain.exceptions.HtmlToPdfConversionException;

import java.io.File;
import java.io.IOException;

/**
 * Converts HTML documents to PDF documents.
 *
 * <p>Under the hood this uses Apache Exec and a synchronization-block to ensure a thread-safe, non-locking
 * interaction between this class and the command line.
 *
 * @see <a href="https://wkhtmltopdf.org/">wkhtmltopdf site</href>
 */
@ThreadSafe
public class HtmlToPdfFileConverter {
    private static final String EXPECT_FILE_AS_STREAM_FROM_STD_IN = "-";
    private static final Object LOCK = new Object[0];

    //TODO: Check response code from wkhtmltopdf and output error message from console

    @GuardedBy("HtmlToPdfFileConverter.LOCK")
    protected final File wkHtmlToPdfBinary;

    public HtmlToPdfFileConverter(File wkHtmlToPdfBinary) {
        this.wkHtmlToPdfBinary = wkHtmlToPdfBinary;
    }

    public HtmlToPdfFileConverter() {
        this(new HtmlToPdfBinaryResolver().resolve());
    }

    /**
     * Generates a PDF from the provided HTML and writes it to a temporary file.
     *
     * <p>Instead of writing the HTML to file first, it is piped directly from memory into the
     * wkhtmltopdf binary, saving us some disk IO.  For more information, see the {@link HtmlPumpStreamHandler}.
     *
     * <p>The resulting {@link PdfFile} implements {@link java.io.Closeable} to simplify deleting the file on disk after we're done with it.
     *
     * <p>Usage example:
     * <pre>
     *          try (PdfFile pdfFile = htmlToPdfFileConverter.convert(html)) {
     *              ...
     *          } catch (IOException exception) {
     *              log.error("An error occurred in the HTML -> PDF conversion.", exception);
     *              throw exception;
     *          }
     * </pre>
     *
     * @return a {@link PdfFile} containing a reference to the PDF file that was created
     */
    public PdfFile tryToConvert(String html) throws IOException {
        return tryToConvert(html, File.createTempFile("pdf", ".pdf"));
    }

    /**
     * Generates a PDF from the provided HTML and writes it to the provided outputFile.
     *
     * <p>Instead of writing the HTML to file first, it is piped directly from memory into the
     * wkhtmltopdf binary, saving us some disk IO.  For more information, see the {@link HtmlPumpStreamHandler}.
     *
     * <p>The resulting {@link PdfFile} implements {@link java.io.Closeable} to simplify deleting the file on disk after we're done with it.
     *
     * <p>Usage example:
     * <pre>
     *          try (PdfFile pdfFile = htmlToPdfFileConverter.convert(html)) {
     *              ...
     *          } catch (IOException exception) {
     *              log.error("An error occurred in the HTML -> PDF conversion.", exception);
     *              throw exception;
     *          }
     * </pre>
     *
     * @return a {@link PdfFile} containing a reference to the PDF file that was created
     */
    public PdfFile tryToConvert(String html, File outputFile) throws HtmlToPdfConversionException {
        synchronized (LOCK) {
            CommandLine conversionCommand = createConversionCommand(outputFile);

            return tryToExecuteCommand(
                    conversionCommand,
                    html,
                    outputFile
            );
        }
    }

    protected CommandLine createConversionCommand(File outputFile) {
        return new CommandLine(wkHtmlToPdfBinary)
                .addArgument(EXPECT_FILE_AS_STREAM_FROM_STD_IN)
                .addArgument(outputFile.getAbsolutePath());
    }

    protected PdfFile tryToExecuteCommand(CommandLine conversionCommand, String html, File outputFile) throws HtmlToPdfConversionException {
        DefaultExecutor executor = new DefaultExecutor();

        Try.withResources(() -> new HtmlPumpStreamHandler(html))
                .of(htmlStreamHandler -> executeCommand(executor, htmlStreamHandler, conversionCommand))
                .getOrElseThrow(ex -> new HtmlToPdfConversionException(executor, ex));

        return new PdfFile(outputFile);
    }

    protected int executeCommand(Executor executor, PumpStreamHandler streamHandler, CommandLine command) throws IOException {
        executor.setWatchdog(createCommandLineWatchdog());
        executor.setStreamHandler(streamHandler);
        return executor.execute(command);
    }

    protected ExecuteWatchdog createCommandLineWatchdog() {
        return new ExecuteWatchdog(15_000);
    }
}
