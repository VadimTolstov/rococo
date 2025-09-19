package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.api.core.ThreadSafeCookiesStore;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookieJarExtension implements AfterTestExecutionCallback {
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ThreadSafeCookiesStore.INSTANCE.removeAll();
    }
}
