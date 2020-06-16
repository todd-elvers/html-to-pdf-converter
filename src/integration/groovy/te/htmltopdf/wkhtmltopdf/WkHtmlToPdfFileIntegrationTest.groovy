package te.htmltopdf.wkhtmltopdf


import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.chrome.ChromeHtmlToPdfConverterIntegrationTest

class WkHtmlToPdfFileIntegrationTest extends WkHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(testInput)
    }
}