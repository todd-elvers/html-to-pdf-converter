package te.htmltopdf.chrome;

import io.vavr.control.Try;
import java.awt.Desktop;
import java.io.File;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import te.htmltopdf.domain.PdfFile;

public class ExampleUsage {

    public static void main(String[] args) {
        HtmlToPdfFileConverter converter = new HtmlToPdfFileConverter();

        File inputFile = new File(HtmlToPdfFileConverter.INPUT_FILE_PATH);
        File outputFile = Try.of(() -> Files.createTempFile("output-file-", ".pdf")).get().toFile();

        System.out.println("Input: " + inputFile.getAbsolutePath());
        System.out.println("Output: " + outputFile.getAbsolutePath() + "\n");

        // Generate the PDF contents
        PdfFile best = converter.convert(inputFile);

        System.out.println("PDF generation complete.");

        // Generate a temp file to store the PDF contents in
        File bestTemp = Try
            .of(() -> Files.createTempFile("best-test-", ".pdf"))
            .get()
            .toFile();

        // Write the PDF contents to said temp file
        Try.run(() -> best.writeToOutputStream(FileUtils.openOutputStream(bestTemp, false)))
            .get();

        System.out.println("Opening Best:");
        open(best.getFileOnDisk());

//        System.out.println("Copying input file to output file directly...");
//        String pdfContents = Try
//            .of(() -> FileUtils.readFileToByteArray(inputFile))
//            .mapTry((bytes) -> Base64.getEncoder().encodeToString(bytes))
//            .get();
//
//        Try.run(() -> FileUtils.writeByteArrayToFile(
//            outputFile,
//            Base64.getDecoder().decode(pdfContents), false)
//        ).get();
//
//        System.out.println("Opening output file @ " + outputFile.getAbsolutePath());
//        open(outputFile);
    }

    private static void open(File file) {
        Try.run(() -> Desktop.getDesktop().open(file));
    }


}
