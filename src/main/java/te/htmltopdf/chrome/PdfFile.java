//package te.htmltopdf.chrome;
//
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.Closeable;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import static org.apache.commons.io.FileUtils.deleteQuietly;
//
///**
// * This class is a wrapper around a File object and exists so that we can abstract away the
// * process of writing the PDF file to an output stream and deleting it from disk afterwords.
// *
// * <p>The {@link Closeable} interface is implemented here so we can utilize try-with-resources.
// */
//public class PdfFile implements Closeable {
//    private static final Logger log = LoggerFactory.getLogger(PdfFile.class);
//
//    private final File fileOnDisk;
//
//    public PdfFile(File fileOnDisk) {
//        this.fileOnDisk = fileOnDisk;
//    }
//
//    public void writeToOutputStream(OutputStream outputStream) throws IOException {
//        try (InputStream inputStream = FileUtils.openInputStream(fileOnDisk)) {
//            IOUtils.copy(inputStream, outputStream);
//        }
//    }
//
//    public long getContentLength() {
//        return fileOnDisk.length();
//    }
//
//    @Override
//    public void close() throws IOException {
//        deleteQuietly(fileOnDisk);
//        log.info("PDF temp. file deleted.");
//    }
//}