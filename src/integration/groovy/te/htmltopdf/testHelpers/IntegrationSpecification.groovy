package te.htmltopdf.testHelpers


import org.apache.commons.io.FileUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import te.htmltopdf.WritablePDF

import java.awt.*

abstract class IntegrationSpecification extends Specification implements ResourceFinding {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    abstract String getTestFileName()
    abstract int getExpectedSizeOfPDF()
    abstract WritablePDF convertToPDF(File testInput)

    // To see the generated PDFs after integration tests run set this to true
    boolean openResultsInBrowser = false

    def "can generate a PDF"() {
        given: 'our static test page for PDF generation'
            File inputFile = findResourceFile(getTestFileName())

        when: "we convert the HTML contents of said page to PDF"
            WritablePDF writablePDF = convertToPDF(inputFile)

        and: "write said PDF to a temporary file"
            File tempOutputFile = temporaryFolder.newFile("temp-file-result.pdf")
            writablePDF.writeToOutputStream(
                    FileUtils.openOutputStream(tempOutputFile, false)
            )

        then: "nothing went wrong in the process"
            noExceptionThrown()

        and: "the output file is the correct size" \
            //TODO: Potentially accept an env var or sys prop that enables this too
            if (openResultsInBrowser) Desktop.getDesktop().open(tempOutputFile)
            FileUtils.sizeOf(tempOutputFile) == getExpectedSizeOfPDF()
    }

}
