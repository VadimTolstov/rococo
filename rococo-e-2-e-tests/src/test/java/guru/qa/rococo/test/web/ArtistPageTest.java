package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestContent;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

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
  void artistShouldBeShown(BufferedImage image, TestContent content) {
    final String artistName = content.artists().getFirst().name();
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
  void searchShouldWorkAndOpenDetail(TestContent content) {
    final String artistName = content.artists().getFirst().name();
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
        .checkingThePagination();
  }

  @Test
  @DisplayName("Если карточка не найдена, отображается соответсвующий текст")
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

  @Test
  @DisplayName("Проверяем отображения текста, когда список художников пуст")
  void checkMessageArtisEmpty() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .checkMessageArtisEmpty();
  }
}
