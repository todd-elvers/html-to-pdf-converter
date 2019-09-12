package te.htmltopdf.chrome;

import com.github.kklisura.cdt.launch.ChromeLauncher;
import com.github.kklisura.cdt.services.ChromeDevToolsService;
import com.github.kklisura.cdt.services.ChromeService;
import com.github.kklisura.cdt.services.exceptions.ChromeServiceException;
import com.github.kklisura.cdt.services.types.ChromeTab;
import io.vavr.Lazy;
import io.vavr.control.Try;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import te.htmltopdf.chrome.domain.exceptions.ChromeTabExecutionException;
import te.htmltopdf.chrome.domain.exceptions.ChromeTabInitializationException;

public class ChromeServiceWrapper {

    private static final Logger log = LoggerFactory.getLogger(ChromeServiceWrapper.class);

    private Lazy<ChromeLauncher> chromeLauncher = Lazy.of(ChromeLauncher::new);
    private Lazy<ChromeService> chromeService = Lazy.of(() -> chromeLauncher.get().launch(true));

    //TODO: Could use a better name now that the return type is String
    public String doInChromeTab(BiFunction<ChromeTab, ChromeDevToolsService, String> chromeTabCallback) {
        ChromeTab tab = createChromeTab();
        ChromeDevToolsService devTools = createDevToolsService(tab);

        return Try.of(() -> chromeTabCallback.apply(tab, devTools))
            .andThen(() -> closeTab(tab, devTools))
            .onFailure(throwable -> log.error(ChromeTabExecutionException.MESSAGE, throwable))
            .getOrElseThrow(ChromeTabExecutionException::new);
    }

    /**
     * Helper method for closing Chrome tabs that were not created within the safe confines of the {@link
     * #doInChromeTab(BiFunction)} method.
     *
     * @param tab             the Chrome tab to close, or null if there isn't one
     * @param devToolsService the connection to the dev tools service to close, or null if there is one
     */
    public void closeTab(ChromeTab tab, ChromeDevToolsService devToolsService) {
        if (devToolsService != null) {
            Try.run(devToolsService::close)
                .andThen(devToolsService::waitUntilClosed)
                .get();
        }

        if (tab != null) {
            Try.run(() -> chromeService.get().closeTab(tab))
                .get();
        }
    }

    public ChromeTab createChromeTab() {
        return Try.of(() -> chromeService.get().createTab())
            .onSuccess(tab -> log.debug("Chrome tab initialized."))
            .getOrElseThrow(ChromeTabInitializationException::new);
    }

    public ChromeDevToolsService createDevToolsService(ChromeTab tab) throws ChromeServiceException {
        return Try.of(() -> chromeService.get().createDevToolsService(tab))
            .onSuccess(devTools -> log.debug("Chrome DevTools initialized."))
            .onFailure(ex -> closeTab(tab, null))
            .getOrElseThrow(ChromeTabInitializationException::new);
    }
}