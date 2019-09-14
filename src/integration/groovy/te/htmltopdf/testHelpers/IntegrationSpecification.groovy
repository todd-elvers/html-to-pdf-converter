package te.htmltopdf.testHelpers

import org.apache.commons.io.FileUtils
import spock.lang.Specification
import te.core.env.MultiPropertyReader
import te.htmltopdf.WritablePDF

import java.awt.*
import java.nio.file.Files

abstract class IntegrationSpecification extends Specification implements ResourceFinding {

    public static final ArrayList<String> VIEW_OUTPUT_FLAGS = ["showPDFs", "showPDF", "showOutput"]

    abstract String getTestFileName()
    abstract int getExpectedSizeOfPDF()
    abstract WritablePDF convertToPDF(File testInput)

    /**
     * Useful for manually verifying the contents of PDFs generated during integration tests.
     *
     * When this returns true then at the end of each integration test (i.e. at the end of tests
     * that extend this base class) the PDF that is generated will be opened using the system's
     * default PDF viewing application.  Set the environment variable, or system property,
     * {@link #VIEW_OUTPUT_FLAGS} to anything (the value does not matter, just as long as the
     * variable/property is present) to trigger this behavior.
     */
    private static boolean openResultsInBrowser() {
        return new MultiPropertyReader().containsAnyKeyIgnoringCase(
                VIEW_OUTPUT_FLAGS
        )
    }

    def "can generate a PDF"() {
        given: 'our static test page for PDF generation'
            File inputFile = findResourceFile(getTestFileName())

        when: "we convert the HTML contents of said page to PDF"
            WritablePDF writablePDF = convertToPDF(inputFile)

        and: "write said PDF to a temporary file"
            File tempOutputFile = Files.createTempFile("temp-file-result", ".pdf").toFile()
            writablePDF.write(
                    FileUtils.openOutputStream(tempOutputFile, false)
            )

        then: "nothing went wrong in the process"
            noExceptionThrown()

        and: "the output file is the correct size"
            FileUtils.sizeOf(tempOutputFile) == getExpectedSizeOfPDF()

        and: "if necessary: open the PDF with the system's default PDF viewer"
            if (openResultsInBrowser()) {
                Desktop.getDesktop().open(tempOutputFile)
                Thread.sleep(2_000)     // Give the system a moment so we don't get a 404
            }

        cleanup:
            FileUtils.deleteQuietly(tempOutputFile)
    }

}
