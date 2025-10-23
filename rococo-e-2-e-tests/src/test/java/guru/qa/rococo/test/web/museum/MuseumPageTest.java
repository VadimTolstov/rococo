package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class MuseumPageTest {

  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Добавить музей")
  void unauthorizedUserDoesNotHaveButtonAddArtist() {
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
    final String museum = content.museums().getFirst().title();
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
        .checkMessageArtistNotFound();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму создания музея")
  void authorizedUserShouldCanOpenAddArtistForm() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded();
  }

  @DisplayName("Проверка пагинации на странице 'Музеи'")
  @DisplayName("Карточка художника отображаются на странице 'Художники'")
  @DisplayName("Проверяем отображения текста, когда список художников пуст")

}
