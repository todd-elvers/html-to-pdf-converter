package te.htmltopdf

import spock.lang.Subject
import te.htmltopdf.testHelpers.IntegrationSpecification

class WkHtmlToPdfConverterIntegrationTest extends IntegrationSpecification {

    @Subject
    WkHtmlToPdfConverter htmlToPdfConverter = []

    final String testFileName = "wkhtmltopdf-test-page.htm"
    final int expectedSizeOfPDF = 99_015

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(testInput.text)
    }
}
