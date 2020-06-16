package te.htmltopdf.chrome

import spock.lang.Subject
import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.chrome.ChromeHtmlToPdfConverter
import te.htmltopdf.testHelpers.IntegrationSpecification

abstract class ChromeHtmlToPdfConverterIntegrationTest extends IntegrationSpecification {

    @Subject
    ChromeHtmlToPdfConverter htmlToPdfConverter = []

    final String testFileName = "chrome-test-page.html"
    final int minimumSizeOfPDF = 30_000


}