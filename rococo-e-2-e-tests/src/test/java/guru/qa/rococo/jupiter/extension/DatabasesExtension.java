package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.data.jdbc.Connections;
import guru.qa.rococo.data.jpa.EntityManagers;
import guru.qa.rococo.service.db.ArtistDbClient;
import guru.qa.rococo.service.db.MuseumDbClient;
import guru.qa.rococo.service.db.PaintingDbClient;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DatabasesExtension implements SuiteExtension {

  @Override
  public void beforeSuite(ExtensionContext context) {
      new PaintingDbClient().removeAll();
      new ArtistDbClient().removeAll();
      new MuseumDbClient().removeAll();
  }

  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
    EntityManagers.closeAllEmfs();
  }
}