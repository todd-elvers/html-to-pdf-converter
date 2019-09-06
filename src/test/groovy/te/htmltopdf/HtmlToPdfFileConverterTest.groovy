package te.htmltopdf

import groovy.transform.InheritConstructors
import org.apache.commons.exec.CommandLine
import spock.lang.Specification
import spock.lang.Subject
import te.htmltopdf.domain.exceptions.HtmlToPdfConversionException

class HtmlToPdfFileConverterTest extends Specification {

    @Subject
    HtmlToPdfFileConverter htmlToPdfFileConverter = []

    def "commandCustomizer is called before our command is executed"() {
        given:
            File outputFile = Mock(File)
            String html = "<html></html>"

        when: 'we call the 3 arg function with a customizer that returns null'
            htmlToPdfFileConverter.tryToConvert(html, outputFile, { CommandLine commandLine ->
                null
            })

        then: 'a null pointer is thrown wrapped in our exception'
            def ex = thrown(HtmlToPdfConversionException)
            ex.cause instanceof NullPointerException

        when: 'we call the 2 arg function with a customizer that returns null'
            htmlToPdfFileConverter.tryToConvert(html, { CommandLine commandLine ->
                null
            })

        then: 'a null pointer is thrown wrapped in our exception'
            ex = thrown(HtmlToPdfConversionException)
            ex.cause instanceof NullPointerException

        when: 'we call the 3 arg function with a customizer that throws an exception'
            htmlToPdfFileConverter.tryToConvert(html, outputFile, { CommandLine commandLine ->
                throw new BogusException()
            })

        then: 'the customizer is called and our exception is thrown'
            thrown(BogusException)

        when: 'we call the 2 arg function with a customizer that throws an exception'
            htmlToPdfFileConverter.tryToConvert(html, { CommandLine commandLine ->
                throw new BogusException()
            })

        then: 'the customizer is called and our exception is thrown'
            thrown(BogusException)
    }

    @InheritConstructors
    static class BogusException extends RuntimeException {}
}
