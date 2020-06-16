package te.htmltopdf.chrome

import org.apache.commons.io.FileUtils
import te.htmltopdf.OutputStreamWritable

class ChromeInputStreamIntegrationTest extends ChromeHtmlToPdfConverterIntegrationTest {

    @Override
    OutputStreamWritable convertToPDF(File testInput) {
        return htmlToPdfConverter.tryToConvert(
                FileUtils.openInputStream(testInput)
        )
    }

}