package guru.qa.rococo.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.api.ArtistApiClient;
import guru.qa.rococo.service.api.PaintingApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class PaintingExtension implements BeforeEachCallback {
  private static final Config CFG = Config.getInstance();
  private final String IMAGE_DIR = "paintings";

  private final PaintingClient paintingClient = new PaintingApiClient();
  private final ArtistClient artistClient = new ArtistApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          Allure.step("Создание картин перед тестами", () -> {
            if (ArrayUtils.isNotEmpty(content.paintings()) || content.paintingCount() > 0) {
              final List<PaintingJson> createPaintings = new ArrayList<>();

              for (Painting paintingAnno : content.paintings()) {
                final String title = "".equals(paintingAnno.title())
                    ? RandomDataUtils.painting()
                    : paintingAnno.title();

                final String artistName = "".equals(paintingAnno.artist())
                    ? RandomDataUtils.randomArtistName()
                    : paintingAnno.artist();

                final ArtistJson artistJson = ContentExtension.getContent()
                    .artists()
                    .stream()
                    .filter(artist -> artistName.equalsIgnoreCase(artist.name()))
                    .findFirst()
                    .orElse(
                        artistClient.getListArtists(artistName, null, null, null)
                            .stream()
                            .filter(artist -> artistName.equalsIgnoreCase(artist.name()))
                            .findFirst()
                            .orElse(
                                artistClient.createArtist(
                                    new ArtistJson(
                                        null,
                                        artistName,
                                        RandomDataUtils.shortBio(),
                                        RandomDataUtils.randomImageString(IMAGE_DIR)
                                    )
                                )
                            )
                    );

                final String museum = "".equals(paintingAnno.museum())
                    ? RandomDataUtils.museum()
                    : paintingAnno.museum();

                final PaintingJson painting = paintingClient.getPaintings(null, null, null, title)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {
                          new PaintingJson(
                              null,
                              title,
                              "".equals(paintingAnno.description())
                                  ? RandomDataUtils.shortBio()
                                  : paintingAnno.description(),
                              "".equals(paintingAnno.content())
                                  ? RandomDataUtils.randomImageString(IMAGE_DIR)
                                  : PhotoConverter.loadImageAsString(CFG.imageContentBaseDir() + paintingAnno.content()),
                              "".equals(paintingAnno.artist())
                                  ? RandomDataUtils.
                          );
                        }
                    );

              }
            }
          });
        });

  }
}
