package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.model.TestContent;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.api.ArtistApiClient;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Allure;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ArtistExtension implements BeforeEachCallback, ParameterResolver {
  private static final Config CFG = Config.getInstance();
  private final String IMAGE_DIR = "artists";
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ArtistExtension.class);
  private final ArtistClient artistClient = new ArtistApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          Allure.step("Создаем художников перед тестом", () -> {
            if (ArrayUtils.isNotEmpty(content.artists()) || content.artistCount() > 0) {
              final List<ArtistJson> createdArtist = new ArrayList<>();

              for (Artist artistAnno : content.artists()) {
                final ArtistJson artist = new ArtistJson(
                    null,
                    "".equals(artistAnno.name())
                        ? RandomDataUtils.randomArtistName()
                        : artistAnno.name(),
                    "".equals(artistAnno.biography())
                        ? RandomDataUtils.shortBio()
                        : artistAnno.biography(),
                    "".equals(artistAnno.photo())
                        ? RandomDataUtils.randomImageString(IMAGE_DIR)
                        : PhotoConverter.loadImageAsString(CFG.imageContentBaseDir() + artistAnno.photo())
                );
                createdArtist.add(
                    artistClient.createArtist(artist)
                );
              }
              for (int i = 0; i < content.artistCount(); i++) {
                createdArtist.add(
                    artistClient.createArtist(
                        new ArtistJson(
                            null,
                            RandomDataUtils.randomArtistName(),
                            RandomDataUtils.shortBio(),
                            RandomDataUtils.randomImageString(IMAGE_DIR)
                        )
                    )
                );

              }
              final TestContent created = new TestContent(createdArtist, new ArrayList<>(), new ArrayList<>());
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  created);
            }
          });
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getRequiredTestMethod().isAnnotationPresent(Content.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(TestContent.class);
  }

  @Override
  public TestContent resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), TestContent.class);
  }
}
