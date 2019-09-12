package te.htmltopdf.wkhtmltopdf

import io.vavr.control.Option
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Subject
import spock.util.environment.RestoreSystemProperties
import te.htmltopdf.wkhtmltopdf.domain.exceptions.MakingFileExecutableException

import java.nio.file.AccessMode
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.spi.FileSystemProvider

import static WkHtmlToPdfBinaryResolver.BINARY_ENV_VAR_NAME

class WkHtmlToPdfBinaryResolverTest extends Specification {

    @Subject
    WkHtmlToPdfBinaryResolver binaryResolver = [Mock(WkHtmlToPdfBinaryExtractor)]

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    void "always ensures the binary is instantiated with the executable flag"() {
        given: 'a mock binary that reports the executable flag as false'
            File wkHtmlToPdfBinary = newMockFile(false)
            assert !Files.isExecutable(wkHtmlToPdfBinary.toPath())

        and: 'a mocked binaryExtractor that returns our mock'
            binaryResolver.binaryExtractor.extract() >> wkHtmlToPdfBinary

        when: 'the binary is resolved'
            def webkitHtmlToPdfExecutable = binaryResolver.resolve()

        then: 'the executable flag on the resolved binary is set to true'
            Files.isExecutable(webkitHtmlToPdfExecutable.toPath())
    }

    void "wraps any exception thrown when trying to make a file executable"() {
        given: 'a non-executable file that throws an exception when permissions are changed'
            File input = newMockFile(false, false)
            input.setExecutable(true) >> { throw new SecurityException("mock-exception") }
            assert !Files.isExecutable(input.toPath())

        when:
            binaryResolver.makeExecutable(input)

        then:
            def ex = thrown(MakingFileExecutableException)
            ex.cause instanceof SecurityException
            ex.cause.message == "mock-exception"
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
    void "will use a custom binary path over the bundled binary when one is provided"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            System.setProperty(BINARY_ENV_VAR_NAME, binaryFilenameFromEnv)

        when:
            File binary = binaryResolver.resolve()

        then:
            binary.name == binaryFilenameFromEnv

        and: 'we did not attempt to extract any binaries from our JAR'
            0 * binaryResolver.binaryExtractor.extract()
    }


    void "will fallback to the bundled binary from when no custom binary path is provided"() {
        given: 'some binary we will pretend exists in some JAR'
            def binaryFromJAR = newMockFile(false)

        when: 'we resolve the binary w/ no environment variables or system properties'
            def resolvedBinary = binaryResolver.resolve()

        then: 'we end up resolving the binary from the JAR and extracting it'
            resolvedBinary == binaryFromJAR
            1 * binaryResolver.binaryExtractor.extract() >> binaryFromJAR

        and: 'will always ensure the file is executable'
            1 * binaryFromJAR.setExecutable(true) >> true
    }

    private File newMockFile(boolean isExecutable, boolean mockSetMethod = true) {
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

            if (mockSetMethod) {
                setExecutable(true) >> {
                    shouldThrowException = false
                    return true
                }
            }

        }
    }

}
