package te.htmltopdf

import te.htmltopdf.HtmlToPdfFileConverter
import te.htmltopdf.testHelpers.ResourceFinding
import org.apache.commons.io.FileUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class HtmlToPdfFileConverterIntegrationTest extends Specification implements ResourceFinding {

    HtmlToPdfFileConverter htmlToPdfFileConverter = new HtmlToPdfFileConverter()

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    void "can generate a PDF from HTML with no relative links"() {
        given:
            String html = findResourceFile("example.htm").getText()
            File outputFile = temporaryFolder.newFile("example.pdf")

        when:
            htmlToPdfFileConverter.tryToConvert(html, outputFile)

        then: 'the PDF is generated and is not empty (should be greater than 100KB)'
            outputFile.exists()
            outputFile.length() > FileUtils.ONE_KB

        and:
            noExceptionThrown()
    }

}