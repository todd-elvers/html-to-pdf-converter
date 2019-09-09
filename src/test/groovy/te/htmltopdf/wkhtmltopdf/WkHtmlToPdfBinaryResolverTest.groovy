package te.htmltopdf.wkhtmltopdf

import io.vavr.control.Option
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.RestoreSystemProperties
import te.htmltopdf.domain.exceptions.MakingFileExecutableException

import java.nio.file.AccessMode
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider

import static te.htmltopdf.wkhtmltopdf.WkHtmlToPdfBinaryResolver.BINARY_ENV_VAR_NAME

class WkHtmlToPdfBinaryResolverTest extends Specification {

    @Subject
    WkHtmlToPdfBinaryResolver binaryResolver = [Mock(WkHtmlToPdfBinaryExtractor)]

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    void "always ensures the binary is instantiated with the executable flag"() {
        given: 'a mock binary that has the executable flag set to false'
            File wkHtmlToPdfBinary = newMockFile(false)
            assert !Files.isExecutable(wkHtmlToPdfBinary.toPath())

        and: 'a binaryExtractor returns our mock file'
            binaryResolver.binaryExtractor.extract() >> wkHtmlToPdfBinary

        when: 'the binary is resolved'
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
        boolean shouldThrowException = !isExecutable

        return Mock(File) {
            toPath() >> Mock(Path) {
                getFileSystem() >> Mock(FileSystem) {
                    provider() >> Mock(FileSystemProvider) {
                        checkAccess(_ as Path, _ as AccessMode[]) >> { file, permissionsToCheck ->
                            if (shouldThrowException) throw new IOException("Access denied, etc.")
                        }
                    }
                }
            }

            setExecutable(true) >> {
                shouldThrowException = false
                return true
            }
        }
    }

    void "can determine when a custom binary path is set"() {
        expect:
            binaryResolver.checkForCustomBinaryPath(envVariables, systemProperties) == expected

        where:
            envVariables                     | systemProperties                                 || expected
            [:]                              | new Properties([:])                              || Option.none()
            [(BINARY_ENV_VAR_NAME): "path1"] | new Properties([:])                              || Option.some("path1")
            [:]                              | new Properties([(BINARY_ENV_VAR_NAME): "path2"]) || Option.some("path2")
            [(BINARY_ENV_VAR_NAME): "path1"] | new Properties([(BINARY_ENV_VAR_NAME): "path2"]) || Option.some("path1")
    }


    @RestoreSystemProperties
    void "will use binary from environment variable over binary from JAR"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            System.setProperty(BINARY_ENV_VAR_NAME, binaryFilenameFromEnv)
            WkHtmlToPdfBinaryResolver binaryResolver = new WkHtmlToPdfBinaryResolver()

        when:
            File binary = binaryResolver.resolve()

        then:
            binary.name == binaryFilenameFromEnv
    }

    void "will fallback to binary from JAR if binary from environment variable is invalid"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            WkHtmlToPdfBinaryResolver binaryResolver = new WkHtmlToPdfBinaryResolver()

        when:
            File binary = binaryResolver.resolve()

        then:
            binary.exists()
            binary.name != binaryFilenameFromEnv
    }

}
