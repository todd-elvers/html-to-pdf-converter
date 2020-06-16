package te.htmltopdf.wkhtmltopdf

import org.apache.commons.io.FileUtils
import te.htmltopdf.OutputStreamWritable
import te.htmltopdf.chrome.ChromeHtmlToPdfConverterIntegrationTest

class WkHtmlToPdfInputStreamIntegrationTest extends ChromeHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(
                FileUtils.openInputStream(testInput)
        )
    }

}