//package te.htmltopdf.chrome;
//
//import com.github.kklisura.cdt.launch.ChromeLauncher
//import com.github.kklisura.cdt.services.ChromeDevToolsService
//import com.github.kklisura.cdt.services.ChromeService
//import com.github.kklisura.cdt.services.types.ChromeTab
//import groovy.transform.CompileStatic
//import org.springframework.stereotype.Component
//
//@CompileStatic
//@Component
//class ChromeServiceWrapper {
//
//    private ChromeService chromeService
//    private ChromeLauncher chromeLauncher
//
//    private ChromeLauncher getChromeLauncher() {
//        if (!isChromeAlive()) {
//            chromeLauncher = new ChromeLauncher()
//        }
//        return chromeLauncher
//    }
//
//    private ChromeService getChromeService() {
//        if (!isChromeAlive() || !chromeService) {
//            chromeService = getChromeLauncher().launch(true)
//        }
//        return chromeService
//    }
//
//    boolean isChromeAlive() {
//        return chromeLauncher && chromeLauncher.isAlive()
//    }
//
//    void closeTab(ChromeTab tab) {
//        if (tab) {
//            chromeService.closeTab(tab)
//        }
//    }
//
//    ChromeTab getChromeTab() {
//        return getChromeService().createTab()
//    }
//
//    ChromeDevToolsService getChromeDevToolsService(ChromeTab tab) {
//        return getChromeService().createDevToolsService(tab)
//    }
//}