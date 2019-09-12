package te.htmltopdf.wkhtmltopdf.domain

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class WritablePDFFileTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Shared
    String mockFileContents = "MOCK PDF FILE CONTENTS"

    def "can write the underlying PDF to any output stream"() {
        given:
            WritablePDFFile pdfFile = new WritablePDFFile(newPDF())
            File newFile = temporaryFolder.newFile()

        when:
            newFile.withOutputStream { outputStream ->
                pdfFile.writeToOutputStream(outputStream)
            }

        then:
            newFile.exists()
            newFile.text == mockFileContents
    }

    def "can return a reference to the underlying PDF"() {
        when:
            WritablePDFFile pdfFile = new WritablePDFFile(newPDF())

        then:
            pdfFile.temporaryFile.exists()
            pdfFile.temporaryFile.text == mockFileContents
    }

    def "calling close() deletes the underlying PDF"() {
        when:
            WritablePDFFile pdfFile = new WritablePDFFile(newPDF())

        then:
            pdfFile.temporaryFile.exists()

        when:
            pdfFile.close()

        then:
            !pdfFile.temporaryFile.exists()
    }

    private File newPDF() {
        File pdf = temporaryFolder.newFile()
        pdf << mockFileContents
        assert pdf.text == mockFileContents
        return pdf
    }
}
