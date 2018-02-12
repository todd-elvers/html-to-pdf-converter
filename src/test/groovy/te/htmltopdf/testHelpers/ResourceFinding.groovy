package te.htmltopdf.testHelpers

import javax.naming.ConfigurationException

// Tool for accessing files in the resources directory during tests
trait ResourceFinding {

    static String findResourcePath(String fileName){
        String filePath = Thread.currentThread().contextClassLoader.getResource(fileName)?.file
        if(!filePath){
            throw new ConfigurationException("Unable to find '$fileName' in resources directory.")
        }
        return filePath
    }

    static File findResourceFile(String fileName) {
        new File(findResourcePath(fileName))
    }

}
