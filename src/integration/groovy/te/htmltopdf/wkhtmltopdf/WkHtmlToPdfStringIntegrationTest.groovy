package te.htmltopdf.wkhtmltopdf

import org.apache.commons.io.FileUtils
import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.chrome.ChromeHtmlToPdfConverterIntegrationTest

import java.nio.charset.Charset

class WkHtmlToPdfStringIntegrationTest extends ChromeHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(
                FileUtils.readFileToString(testInput, Charset.defaultCharset())
        )
    }
}