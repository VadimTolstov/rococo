package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.page.detail.MuseumDetailPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты на страницу с подробной информацией о музеи")
public class MuseumDetailTest {

  @Content(museums = @Museum(photo = "/museums/scale.jpeg"))
  @ScreenShotTest(expected = "museum/museum-detail/scale.png")
  @DisplayName("В детальной информации о музее отображается изображение музея")
  void museumShouldBeShown(BufferedImage image, ContentJson content) {
    final MuseumJson museum = content.museums().getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkImage(image);
  }

  @Content(
      museumCount = 1
  )
  @Step("Подробная информацию о музее отображается")
  @Test
  public void detailedInformationAboutMuseumIsDisplayed(ContentJson content) {
    final MuseumJson museum = content.museums().getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailMuseum(museum);
  }

  @Test
  @User
  @ApiLogin
  @Content(
      museumCount = 1
  )
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму редактирования")
  void authorizedUserShouldCanOpenAddArtistForm(ContentJson content) {
    final MuseumJson museum = content.museums().getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .clickEdit()
        .checkThatComponentLoaded();
  }

  @Content(
      museumCount = 1
  )
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonAddArtist(ContentJson content) {
    final MuseumJson museum = content.museums().getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdateMuseumButton();
  }
}
