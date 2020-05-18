package te.htmltopdf

import spock.lang.Subject
import te.htmltopdf.testHelpers.IntegrationSpecification

class ChromeHtmlToPdfConverterIntegrationTest extends IntegrationSpecification {

    @Subject
    ChromeHtmlToPdfConverter htmlToPdfConverter = []

    final String testFileName = "chrome-test-page.html"
    final int expectedSizeOfPDF = 66_605

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(testInput)
    }
}