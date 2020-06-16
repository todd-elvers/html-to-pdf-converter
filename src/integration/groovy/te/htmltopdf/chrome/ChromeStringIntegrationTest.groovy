package te.htmltopdf.chrome

import org.apache.commons.io.FileUtils
import te.htmltopdf.OutputStreamWritable

import java.nio.charset.Charset

class ChromeStringIntegrationTest extends ChromeHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(
                FileUtils.readFileToString(testInput, Charset.defaultCharset())
        )
    }
}