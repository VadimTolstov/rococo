package guru.qa.rococo.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.Painting;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.Country;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.PaintingClient;
import guru.qa.rococo.service.api.ArtistApiClient;
import guru.qa.rococo.service.api.MuseumApiClient;
import guru.qa.rococo.service.api.PaintingApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Allure;
import lombok.NonNull;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class PaintingExtension implements BeforeEachCallback {
  private static final Config CFG = Config.getInstance();
  private final String IMAGE_DIR_PAINTINGS = "paintings";
  private final String IMAGE_DIR_ARTISTS = "artists";
  private final String IMAGE_DIR_MUSEUMS = "museums";

  private final PaintingClient paintingClient = new PaintingApiClient();
  private final ArtistClient artistClient = new ArtistApiClient();
  private final MuseumClient museumClient = new MuseumApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          Allure.step("Создание картин перед тестами", () -> {
            final List<PaintingJson> paintingJsonList = new ArrayList<>();
            final List<MuseumJson> museumJsonList = new ArrayList<>();
            final List<ArtistJson> artistJsonList = new ArrayList<>();
            final ContentJson contentJson = ContentExtension.getContent();
            if (ArrayUtils.isNotEmpty(content.paintings())) {

              for (Painting paintingAnno : content.paintings()) {
                final String paintingTitle = "".equals(paintingAnno.title())
                    ? RandomDataUtils.painting()
                    : paintingAnno.title();


                final PaintingJson paintingJson = paintingClient.getPaintings(null, null, null, paintingTitle)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> {

                          final String artistName = "".equals(paintingAnno.artist())
                              ? RandomDataUtils.randomArtistName()
                              : paintingAnno.artist();

                          final ArtistJson artistJson = contentJson
                              .artists()
                              .stream()
                              .filter(artist -> artistName.equals(artist.name()))
                              .findFirst()
                              .orElseGet(() ->
                                  artistClient.getPageListArtists(artistName, null, null, null)
                                      .stream()
                                      .filter(artist -> artistName.equalsIgnoreCase(artist.name()))
                                      .findFirst()
                                      .orElseGet(() -> randomArtist(artistName))
                              );
                          artistJsonList.add(artistJson);

                          final String museum = "".equals(paintingAnno.museum())
                              ? RandomDataUtils.museum()
                              : paintingAnno.museum();

                          final MuseumJson museumJson = contentJson
                              .museums()
                              .stream()
                              .filter(museums -> museum.equalsIgnoreCase(museums.title()))
                              .findFirst()
                              .orElseGet(() ->
                                  museumClient.getMuseums(null, null, null, museum)
                                      .stream().filter(museums -> museum.equalsIgnoreCase(museums.title()))
                                      .findFirst()
                                      .orElseGet(() ->
                                          randomMuseum(museum)
                                      )
                              );
                          museumJsonList.add(museumJson);

                          return paintingClient.createPainting(new PaintingJson(
                              null,
                              paintingTitle,
                              "".equals(paintingAnno.description())
                                  ? RandomDataUtils.shortBio()
                                  : paintingAnno.description(),
                              "".equals(paintingAnno.content())
                                  ? RandomDataUtils.randomImageString(IMAGE_DIR_PAINTINGS)
                                  : PhotoConverter.loadImageAsString(CFG.imageContentBaseDir() + paintingAnno.content()),
                              artistJson,
                              museumJson
                          ));
                        }
                    );
                paintingJsonList.add(paintingJson);
              }
            }

            if (content.paintingCount() > 0) {
              final ArtistJson artist = randomArtist(RandomDataUtils.randomArtistName());
              final MuseumJson museum = randomMuseum(RandomDataUtils.museum());
              for (int i = 0; i < content.paintingCount(); i++) {
                paintingJsonList.add(randomPainting(artist, museum));
              }
              artistJsonList.add(artist);
              museumJsonList.add(museum);
            }
            contentJson.artists().addAll(artistJsonList);
            contentJson.museums().addAll(museumJsonList);
            contentJson.paintings().addAll(paintingJsonList);
          });
        });

  }

  private @NonNull MuseumJson randomMuseum(String museum) {
    return museumClient.createMuseum(new MuseumJson(
            null,
            museum,
            RandomDataUtils.shortBio(),
            RandomDataUtils.randomImageString(IMAGE_DIR_MUSEUMS),
            new GeoJson(RandomDataUtils.city(),
                new CountryJson(null,
                    Country.random().getCountry()))

        )
    );
  }

  private @NonNull ArtistJson randomArtist(String artistName) {
    return artistClient.createArtist(
        new ArtistJson(
            null,
            artistName,
            RandomDataUtils.shortBio(),
            RandomDataUtils.randomImageString(IMAGE_DIR_ARTISTS)
        )
    );
  }

  private @NonNull PaintingJson randomPainting(ArtistJson artist, MuseumJson museum) {
    return paintingClient.createPainting(new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR_PAINTINGS),
        artist,
        museum
    ));
  }
}
