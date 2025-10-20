package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.service.ArtistClient;
import org.junit.jupiter.api.extension.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArtistExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
  private final ArtistClient artistClient = new ArtistApiClietn();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {

  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return false;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return null;
  }
}
