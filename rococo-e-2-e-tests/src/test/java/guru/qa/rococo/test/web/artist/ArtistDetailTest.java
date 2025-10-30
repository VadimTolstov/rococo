package guru.qa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.page.detail.ArtistDetailPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на детальную страницу с художником")
public class ArtistDetailTest {

  @Content(
      artistCount = 1
  )
  @Test
  @DisplayName("Подробная информацию о художнике отображается")
  public void detailedInformationAboutArtistDisplayed(ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailsArtist(artist);
  }


  @User
  @ApiLogin
  @Content(
      artistCount = 1
  )
  @Test
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму редактирования")
  void authorizedUserShouldCanOpenAddArtistForm(ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .clickEdit()
        .checkThatComponentLoaded();
  }

  @User
  @ApiLogin
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @ScreenShotTest(expected = "artist/artist-detail/ivan-the-terrible.png")
  @DisplayName("Авторизованный пользователь имеет возможность добавить картину в детальной информации о художнике")
  void authorizedUserShouldCanOpenAddPaintingForm(BufferedImage image, ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(Config.getInstance().imageContentBaseDir() + "paintings/ivan-the-terrible.png")
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(ArtistDetailPage.class)
        .checkImagePaintings(image);

  }

  @Content(
      artistCount = 1
  )
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonAddArtist(ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdateMuseumButton();
  }


  @Test
  @Content(artistCount = 1)
  @DisplayName("Когда у художника нет картин, на детальной странице отображается информация об отсутствии картин")
  void whenArtistHasNotPaintingsThatShownEmptyPaintingsPage(ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .checkMessagePaintingOfEmptyArtistDetailPage();
  }


  @Test
  @Content(paintingCount = 10)
  @DisplayName("Пагинация списка картин на детальной странице художника работает")
  void artistDetailsPaginationShouldWork(ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .getPaginationComponent()
        .checkingThePagination();
  }

  @Content(artists = @Artist(photo = "artists/repin.png"))
  @ScreenShotTest(expected = "artist/artist-detail/repin.png")
  @DisplayName("В детальной информации о художнике отображается изображение художника")
  void artistShouldBeShown(BufferedImage image, ContentJson content) {
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    Selenide.open(ArtistDetailPage.URL + artist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .checkImage(image);
  }
}
