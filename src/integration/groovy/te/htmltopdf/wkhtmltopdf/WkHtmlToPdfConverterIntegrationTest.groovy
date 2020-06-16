package te.htmltopdf.wkhtmltopdf

import spock.lang.Subject
import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.testHelpers.IntegrationSpecification
import te.htmltopdf.wkhtmltopdf.WkHtmlToPdfConverter

abstract class WkHtmlToPdfConverterIntegrationTest extends IntegrationSpecification {

    @Subject
    WkHtmlToPdfConverter htmlToPdfConverter = []

    final String testFileName = "wkhtmltopdf-test-page.htm"
    final int minimumSizeOfPDF = 99_000

}
