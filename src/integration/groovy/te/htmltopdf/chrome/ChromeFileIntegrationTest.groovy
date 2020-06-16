package te.htmltopdf.chrome

import spock.lang.Subject
import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.chrome.ChromeHtmlToPdfConverter
import te.htmltopdf.testHelpers.IntegrationSpecification

class ChromeFileIntegrationTest extends ChromeHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(testInput)
    }
}