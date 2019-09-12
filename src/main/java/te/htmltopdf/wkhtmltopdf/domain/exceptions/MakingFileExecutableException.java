package te.htmltopdf.wkhtmltopdf.domain.exceptions;

import io.vavr.control.Option;

import java.io.File;

/**
 * This represents an error from the file system encountered when this application was
 * attempting to change a file's permissions to be executable.
 */
public class MakingFileExecutableException extends RuntimeException {

    public MakingFileExecutableException(String message) {
        super(message);
    }

    public MakingFileExecutableException(File nonExecutableFile, Throwable exception) {
        super(buildMessage(nonExecutableFile, Option.of(exception)), exception);
    }

    public MakingFileExecutableException(File nonExecutableFile) {
        this(buildMessage(nonExecutableFile, Option.none()));
    }

    private static String buildMessage(File nonExecutableFile, Option<Throwable> exception) {
        return String.format("Failed to make '%s' executable.\nFull path to file: %s%s",
                nonExecutableFile.getName(),
                nonExecutableFile.getAbsolutePath(),
                exception.map(ex -> "\nCause: " + ex.getMessage()).getOrElse("")
        );
    }
}
