package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на страницу музеи")
public class MuseumPageTest {

  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Добавить музей")
  void unauthorizedUserDoesNotHaveButtonAddMuseum() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .checkNoAddMuseumButton();
  }

  @Content(
      museumCount = 10
  )
  @Test
  @DisplayName("Найти музей через поиск и перейти в его описание")
  void searchShouldWorkAndOpenDetail(ContentJson content) {
    final String museum = new ArrayList<>(content.museums()).getFirst().title();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .searchAndOpenPainting(museum)
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Если музей не найдена, отображается соответсвующий текст")
  void museumNotFoundTest() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .getSearchField()
        .searchThroughButton(RandomDataUtils.randomSurname())
        .toPage(MuseumPage.class)
        .checkMessageMuseumNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания музея")
  void authorizedUserShouldCanOpenAddMuseumForm() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded();
  }

  @Content(
      museumCount = 20
  )
  @Test
  @DisplayName("Проверка пагинации на странице 'Музеи'")
  void paginateShouldWork() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
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

}
