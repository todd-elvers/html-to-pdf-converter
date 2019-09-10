package te.htmltopdf.chrome;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.exceptions.ChromeServiceException;
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

    private ChromeService getChromeService() throws ChromeServiceException {
        if (!isChromeAlive() || chromeService == null) {
            chromeService = getChromeLauncher().launch(true);
        }

        return chromeService;
    }

    public boolean isChromeAlive() {
        return chromeLauncher != null && chromeLauncher.isAlive();
    }

    public void closeTab(ChromeTab tab) {
        if (tab != null) {
            chromeService.closeTab(tab);
        }
    }

    public ChromeTab getChromeTab() throws ChromeServiceException {
        return getChromeService().createTab();
    }

    public ChromeDevToolsService getChromeDevToolsService(ChromeTab tab)
        throws ChromeServiceException {
        return getChromeService().createDevToolsService(tab);
    }
}