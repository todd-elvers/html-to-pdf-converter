package te.htmltopdf.pdf

import te.htmltopdf.HtmlToPdfBinaryExtractor
import te.htmltopdf.domain.exceptions.BinaryClassLoaderException
import te.htmltopdf.domain.exceptions.BinaryExtractionException

import spock.lang.Specification
import te.htmltopdf.domain.exceptions.TempFileCreationException

class HtmlToPdfBinaryExtractorTest extends Specification {

    void "throws TempFileCreationException if we fail to create a temp file"() {
        given:
            def binaryExtractor = new HtmlToPdfBinaryExtractor() {
                protected File createNewTempFile(String filename) {
                    throw new TempFileCreationException()
                }
            }

        when:
            binaryExtractor.extract()

        then:
            thrown(TempFileCreationException)
    }

    void "throws a BinaryClassLoaderException if a file cannot be found on the classpath"() {
        given:
            def binaryExtractor = new HtmlToPdfBinaryExtractor() {
                @Override
                protected String determineBinaryFilename() {
                    return "bogus-file-name"
                }
            }

        when:
            binaryExtractor.extract()

        then:
            thrown(BinaryClassLoaderException)
    }

    void "throws a BinaryExtractionException if we cannot open a stream to the binary in the JAR"() {
        given:
            def binaryExtractor = new HtmlToPdfBinaryExtractor() {
                @Override
                protected File createNewTempFile(String filename) {
                    return null
                }
            }

        when:
            binaryExtractor.extract()

        then:
            thrown(BinaryExtractionException)
    }
}
