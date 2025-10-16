package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты главной страницы")
public class MainPageTest {

  @Test
  @DisplayName("При клике по картинке 'Картины' происходит переход на страницу с Картинами")
  void clickPaintingImgShouldOpenPaintingsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickPaintingsLink();
  }

  @Test
  @DisplayName("При клике по картинке 'Музеи' происходит переход на страницу с Музеями")
  void clickMuseumsImgShouldOpenMuseumsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickMuseumsLink();
  }

  @Test
  @DisplayName("При клике по картинке 'Художники' происходит переход на страницу с Художниками")
  void clickArtistsImgShouldOpenArtistsPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .clickArtistsLink();
  }


  @ScreenShotTest(expected = "main/dark-theme.png")
  @DisplayName("На главной странице можно выбрать темную тему")
  void mainPageHasDarkTheme(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkDarkTheme(expected);
  }

  @ScreenShotTest(expected = "main/light-theme.png")
  @DisplayName("На главной странице отображается светлая тему")
  void mainPageHasLightTheme(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkLightTheme(expected);
  }

  @ScreenShotTest(expected = "main/painting-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Картины'")
  void paintingButtonShouldHasImage(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkImagesPainting(expected);
  }

  @ScreenShotTest(expected = "main/museum-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Музеи'")
  void museumButtonShouldHasImage(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkImagesMuseums(expected);
  }

  @ScreenShotTest(expected = "main/artist-main-page.png")
  @DisplayName("На странице есть изображение для компонента 'Художники'")
  void artistButtonShouldHasImage(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .checkThatPageLoaded()
        .checkImagesArtist(expected);
  }
}