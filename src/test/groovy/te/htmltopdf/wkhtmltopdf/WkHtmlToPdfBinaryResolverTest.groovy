package te.htmltopdf.wkhtmltopdf

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject
import te.htmltopdf.domain.exceptions.MakingFileExecutableException

import java.nio.file.AccessMode
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider

class WkHtmlToPdfBinaryResolverTest extends Specification {

    @Subject
    WkHtmlToPdfBinaryResolver binaryResolver = []

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    void "always ensures the binary is instantiated with the executable flag"() {
        given: 'a webkitHtmlToPdf binary missing the executable flag'
            File wkHtmlToPdfBinary = newMockFile(false)
            assert !Files.isExecutable(wkHtmlToPdfBinary.toPath())

        when: 'webkitHtmlToPdf binary bean is instantiated'
            def webkitHtmlToPdfExecutable = binaryResolver.resolve()

        then: 'the executable flag is set'
            Files.isExecutable(webkitHtmlToPdfExecutable.toPath())
    }

    void "wraps any exception thrown when trying to make a file executable"() {
        given: 'a non-executable file that throws an exception when permissions are changed'
            File input = newMockFile(false)
            input.setExecutable(true) >> { throw new SecurityException("mock-exception") }
            assert !Files.isExecutable(input.toPath())

        when:
            binaryResolver.makeExecutable(input)

        then:
            def ex = thrown(MakingFileExecutableException)
            ex.cause instanceof SecurityException
            ex.cause.message == "mock-exception"
    }

    private File newMockFile(boolean isExecutable) {
        return Mock(File) {
            toPath() >> Mock(Path) {
                getFileSystem() >> Mock(FileSystem) {
                    provider() >> Mock(FileSystemProvider) {
                        checkAccess(_ as Path, _ as AccessMode[]) >> { file, permissionsToCheck ->
                            if (!isExecutable) throw new IOException("Access denied, etc.")
                        }
                    }
                }
            }
        }
    }

    void "will use binary from environment variable over binary from JAR"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            WkHtmlToPdfBinaryResolver binaryResolver = new WkHtmlToPdfBinaryResolver() {
                protected boolean isEnvironmentVariableSet() {
                    return true
                }

                @Override
                protected File resolveBinaryFromEnvironmentVariable() {
                    return temporaryFolder.newFile(binaryFilenameFromEnv)
                }
            }

        when:
            File binary = binaryResolver.resolve()

        then:
            binary.exists()
            binary.name == binaryFilenameFromEnv
    }

    void "will fallback to binary from JAR if binary from environment variable is invalid"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            WkHtmlToPdfBinaryResolver binaryResolver = new WkHtmlToPdfBinaryResolver() {
                protected boolean isEnvironmentVariableSet() {
                    return true
                }

                @Override
                protected File resolveBinaryFromEnvironmentVariable() {
                    return new File(binaryFilenameFromEnv)
                }
            }

        when:
            File binary = binaryResolver.resolve()

        then:
            binary.exists()
            binary.name != binaryFilenameFromEnv
    }

}
