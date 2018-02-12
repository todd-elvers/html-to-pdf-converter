package te.htmltopdf.pdf

import spock.lang.IgnoreIf
import te.htmltopdf.HtmlToPdfBinaryResolver
import te.htmltopdf.testHelpers.ResourceFinding
import org.apache.commons.lang3.SystemUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

class HtmlToPdfBinaryResolverTest extends Specification implements ResourceFinding {
    HtmlToPdfBinaryResolver binaryResolver = []

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    @IgnoreIf({ System.properties['os.name']?.toString()?.toLowerCase()?.contains("windows") })
    void "always ensures the binary it is instantiated with has the executable flag"() {
        given: 'a webkitHtmlToPdf binary missing the executable flag'
            File wkHtmlToPdfBinary = getOsSpecificBinary()
            wkHtmlToPdfBinary.setExecutable(false)
            assert !Files.isExecutable(wkHtmlToPdfBinary.toPath())

        when: 'webkitHtmlToPdf binary bean is instantiated'
            def webkitHtmlToPdfExecutable = binaryResolver.resolve()

        then: 'the executable flag is set'
            Files.isExecutable(webkitHtmlToPdfExecutable.toPath())
    }

    @IgnoreIf({ System.properties['os.name']?.toString()?.toLowerCase()?.contains("windows") })
    void "throws RuntimeException if the wkhtmltopdf's executable flag cannot be set"() {
        when:
            binaryResolver.asExecutable(new File('not-going-to-find-this'))

        then:
            thrown(RuntimeException)
    }

    void "will use binary from environment variable over binary from JAR"() {
        given:
            String binaryFilenameFromEnv = "provided-binary"
            HtmlToPdfBinaryResolver binaryResolver = new HtmlToPdfBinaryResolver() {
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
            HtmlToPdfBinaryResolver binaryResolver = new HtmlToPdfBinaryResolver() {
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


    private static File getOsSpecificBinary() {
        if (SystemUtils.IS_OS_MAC) {
            return findResourceFile("wkhtmltopdf_mac")
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return findResourceFile("wkhtmltopdf_win.exe")
        } else {
            return findResourceFile("wkhtmltopdf_linux")
        }
    }


}
