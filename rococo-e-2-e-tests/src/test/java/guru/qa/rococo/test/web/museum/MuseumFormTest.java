package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.museum.Country;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.detail.MuseumDetailPage;
import guru.qa.rococo.page.form.MuseumForm;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты на форму создания/редактирования музея")
public class MuseumFormTest {

  private static final String FOLDER_NAME = "museums";


  @User
  @ApiLogin
  @ScreenShotTest(
      expected = "museum/museum-detail/tyla.png"
  )
  @DisplayName("Авторизованный пользователь может добавить музей")
  void authorizedUserShouldCanAddMuseum(BufferedImage image) {
    final MuseumJson museum = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/tyla.png",
        new GeoJson(
            RandomDataUtils.city(),
            new CountryJson(
                null,
                Country.random().getCountry()
            )
        )
    );
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .addMuseum(museum)
        .checkAlert("Добавлен музей: " + museum.title())
        .openDetailPage(museum.title())
        .checkThatPageLoaded()
        .checkDetailMuseum(museum)
        .checkImage(image);
  }

  @Content(museums = {@Museum(photo = "museums/british-museum.png")})
  @User
  @ApiLogin
  @ScreenShotTest(expected = "museum/museum-detail/tank-museum.png")
  @DisplayName("Авторизованный пользователь может редактировать музей")
  void authorizedUserShouldCanEditMuseum(ContentJson content, BufferedImage expected) {
    final MuseumJson museum = content.museums().getFirst();
    final MuseumJson newMuseum = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/tank-museum.png",
        new GeoJson(
            RandomDataUtils.city(),
            new CountryJson(
                null,
                Country.random().getCountry()
            )
        )
    );
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailMuseum(museum)
        .clickEdit()
        .fullUpdateMuseum(newMuseum)
        .checkAlert("Обновлен музей: " + newMuseum.title())
        .checkDetailMuseum(newMuseum)
        .checkImage(expected);
  }

  @Content(museumCount = 1)
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonUpdateMuseum(ContentJson content) {
    final MuseumJson museum = content.museums().getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdateMuseumButton();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Название музея - не может быть короче 3 символов")
  void titleShouldBeRequired() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomString.make(1))
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Название не может быть короче 3 символов");
  }
}

