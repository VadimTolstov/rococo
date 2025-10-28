package guru.qa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на страницу с картинами")
public class PaintingPageTest {

  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Добавить картину")
  void unauthorizedUserDoesNotHaveButtonAddMuseum() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .checkNoAddPaintingButton();
  }

  @Content(
      paintingCount = 10
  )
  @Test
  @DisplayName("Найти картину через поиск и перейти в его описание")
  void searchShouldWorkAndOpenDetail(ContentJson content) {
    final String painting = new ArrayList<>(content.paintings()).getFirst().title();
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .searchAndOpenPainting(painting)
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Если картина не найдена, отображается соответсвующий текст")
  void museumNotFoundTest() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .getSearchField()
        .searchThroughButton(RandomDataUtils.randomSurname())
        .toPage(PaintingPage.class)
        .checkMessagePaintingNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания картины")
  void authorizedUserShouldCanOpenAddPaintingForm() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded();
  }

  @Content(
      paintingCount = 20
  )
  @Test
  @DisplayName("Проверка пагинации на странице 'Картины'")
  void paginateShouldWork() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .getPaginationComponent()
        .checkingThePagination();
  }

  @Content(museums = @Museum(photo = "/museums/scale.jpeg"))
  @ScreenShotTest(expected = "museum/scale.png")
  @DisplayName("Карточка музея отображаются на странице 'Музеи'")
  void museumShouldBeShown(BufferedImage image, ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .clickSwitchIsWhiteTheme(false, MuseumPage.class)
        .getSearchField()
        .searchThroughEnter(museumName)
        .toPage(MuseumPage.class)
        .checkImage(image, museumName);
  }

  @Test
  @DisplayName("Проверяем отображения текста, когда список музеев пуст")
  void checkMessageArtisEmpty() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .checkMessageMuseumEmpty();
  }
}
