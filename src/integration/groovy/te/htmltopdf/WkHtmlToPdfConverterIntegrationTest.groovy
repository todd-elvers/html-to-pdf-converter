package te.htmltopdf


import spock.lang.Subject
import te.htmltopdf.testHelpers.IntegrationSpecification
import te.htmltopdf.testHelpers.ResourceFinding

import java.nio.file.Files

class WkHtmlToPdfConverterIntegrationTest extends IntegrationSpecification implements ResourceFinding {

    @Subject
    WkHtmlToPdfConverter htmlToPdfConverter = []

    String testFileName = "wkhtmltopdf-test-page.htm"
    int expectedSizeOfPDF = 99_015

    WritablePDF convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(
                testInput.text,
                Files.createTempFile("integration-test-", ".pdf").toFile()
        )
    }
}
