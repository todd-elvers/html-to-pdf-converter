package te.htmltopdf

import spock.lang.Subject
import te.htmltopdf.testHelpers.IntegrationSpecification
import te.htmltopdf.wkhtmltopdf.WkHtmlToPdfConverter

class WkHtmlToPdfConverterIntegrationTest extends IntegrationSpecification {

    @Subject
    WkHtmlToPdfConverter htmlToPdfConverter = []

    final String testFileName = "wkhtmltopdf-test-page.htm"
    final int minimumSizeOfPDF = 99_000

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(testInput.text)
    }
}
