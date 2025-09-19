package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.data.jdbc.Connections;
import guru.qa.rococo.data.jpa.EntityManagers;

public class DatabasesExtension implements SuiteExtension {

    @Override
    public void afterSuite() {
        Connections.closeAllConnections();
        EntityManagers.closeAllEmfs();
    }
}