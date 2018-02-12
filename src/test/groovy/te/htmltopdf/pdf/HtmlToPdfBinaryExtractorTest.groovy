package te.htmltopdf.pdf

import te.htmltopdf.HtmlToPdfBinaryExtractor
import te.htmltopdf.domain.exceptions.BinaryExtractionException

import spock.lang.Specification

class HtmlToPdfBinaryExtractorTest extends Specification {

    void "throws BinaryExtractionException if a file cannot be found in the classpath"() {
        when:
            def binaryExtractor = new HtmlToPdfBinaryExtractor() {
                protected File createNewTempFile(String filename) {
                    return null
                }
            }
            binaryExtractor.extract()

        then:
            thrown(BinaryExtractionException)
    }

}
