package te.htmltopdf.chrome;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.types.ChromeTab;

class ChromeServiceWrapper {

    private ChromeService chromeService;
    private ChromeLauncher chromeLauncher;

    private ChromeLauncher getChromeLauncher() {
        if (!isChromeAlive()) {
            chromeLauncher = new ChromeLauncher();
        }

        return chromeLauncher;
    }

    private ChromeService getChromeService() {
        if (!isChromeAlive() || chromeService != null) {
            chromeService = getChromeLauncher().launch(true);
        }

        return chromeService;
    }

    boolean isChromeAlive() {
        return chromeLauncher != null && chromeLauncher.isAlive();
    }

    void closeTab(ChromeTab tab) {
        if (tab != null) {
            chromeService.closeTab(tab);
        }
    }

    ChromeTab getChromeTab() {
        return getChromeService().createTab();
    }

    ChromeDevToolsService getChromeDevToolsService(ChromeTab tab) {
        return getChromeService().createDevToolsService(tab);
    }
}