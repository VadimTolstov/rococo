package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

@WebTest
@ParametersAreNonnullByDefault
@DisplayName("Тесты на Header")
public class HeaderTest {

  @Test
  @DisplayName("При клике по кнопке 'Картины'  происходит переход на страницу с Картинами")
  void clickPaintingsButtonShouldOpenPaintingsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickPaintingsLink()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("При клике по кнопке 'Музеи'  происходит переход на страницу с Музеями")
  void clickMuseumsButtonShouldOpenMuseumsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickMuseumsLink()
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("При клике по кнопке 'Художники'  происходит переход на страницу с Художниками")
  void clickArtistsButtonShouldOpenArtistsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickArtistsLink()
        .checkThatPageLoaded();
  }

  @User
  @ApiLogin
  @ScreenShotTest(expected = "profile/avatar-template-small.png")
  @DisplayName("У пользователя без аватара должна отображаться заглушка в шапке")
  void avatarTemplateShouldBeShown(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .checkImgAvatar(expected)
    ;
  }

  //todo добавить тест когда будет реализован полноценный @User с аватаром
  @ScreenShotTest(expected = "profile/avatar-template-small.png1", rewriteExpected = true)
  @DisplayName("У пользователя с аватаром аватар должен отображаться в шапке")
  void avatarShouldBeShown(BufferedImage expected) {
  }

  @Test
  @DisplayName("Клик по логотипу переводит на главную страницу")
  void clickLogoShouldOpenMainPage() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .getHeader()
        .clickLogo()
        .checkThatPageLoaded();
  }
}
