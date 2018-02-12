package te.htmltopdf.domain

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

class PdfFileTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Shared
    String mockFileContents = "MOCK PDF FILE CONTENTS"

    def "can write the underlying PDF to any output stream"() {
        given:
            PdfFile pdfFile = new PdfFile(newPDF())
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
            PdfFile pdfFile = new PdfFile(newPDF())

        then:
            pdfFile.fileOnDisk.exists()
            pdfFile.fileOnDisk.text == mockFileContents
    }

    def "calling close() deletes the underlying PDF"() {
        when:
            PdfFile pdfFile = new PdfFile(newPDF())

        then:
            pdfFile.fileOnDisk.exists()

        when:
            pdfFile.close()

        then:
            !pdfFile.fileOnDisk.exists()
    }

    private File newPDF() {
        File pdf = temporaryFolder.newFile()
        pdf << mockFileContents
        assert pdf.text == mockFileContents
        return pdf
    }
}
