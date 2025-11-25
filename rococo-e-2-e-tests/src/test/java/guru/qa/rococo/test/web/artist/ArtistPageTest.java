package guru.qa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@ParametersAreNonnullByDefault
@DisplayName("Тесты на страницу Художники")
public class ArtistPageTest {

  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Добавить Художника'")
  void unauthorizedUserDoesNotHaveButtonAddArtist() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .checkNoAddPaintingButton();
  }

  @Content(
      artists = @Artist(photo = "/artists/vangog.png")
  )
  @ScreenShotTest(expected = "artist/vangog.png")
  @DisplayName("Карточка художника отображаются на странице 'Художники'")
  void artistShouldBeShown(BufferedImage image, ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickSwitchIsWhiteTheme(false, ArtistPage.class)
        .getSearchField()
        .searchThroughEnter(artistName)
        .toPage(ArtistPage.class)
        .checkImage(image, artistName);
  }

  @Content(
      artistCount = 10
  )
  @Test
  @DisplayName("Найти художника через поиск и перейти в его описание")
  void searchShouldWorkAndOpenDetail(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .searchAndOpenPainting(artistName)
        .checkThatPageLoaded();
  }

  @Content(
      artistCount = 20
  )
  @Test
  @DisplayName("Проверяем пагинацию на странице 'Художники'")
  void paginateShouldWork() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .getPaginationComponent()
        .checkingThePagination();
  }

  @Test
  @DisplayName("Если художник не найдена, отображается соответсвующий текст")
  void artistsNotFoundTest() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .getSearchField()
        .searchThroughButton(RandomDataUtils.randomSurname())
        .toPage(ArtistPage.class)
        .checkMessageArtistNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания художника")
  void authorizedUserShouldCanOpenAddArtistForm() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded();
  }

}
