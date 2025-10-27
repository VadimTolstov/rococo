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
  @ScreenShotTest(expected = "museum/museum-detail/tyla.png")
  @DisplayName("Авторизованный пользователь может добавить музей")
  void authorizedUserShouldCanAddMuseum(BufferedImage image) {
    final MuseumJson museum = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/tyla.jpg",
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
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/tank-museum.jpg",
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
        .setTitle(RandomString.make(2))
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Название не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 3 символами в поле 'Название музея'")
  void titleShouldBeMinLength() {
    final String museumTitle = RandomString.make(3);
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumTitle)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumTitle);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 255 символами в поле 'Название музея'")
  void titleShouldBeMaxLength() {
    final String museumTitle = RandomString.make(255);
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumTitle)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumTitle);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Название музея - не может быть длине 255 символов")
  void titleShouldBeNotLong() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomString.make(256))
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Название не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 3 символами в поле 'Укажите город'")
  void citiShouldBeMinLength() {
    final String museumTitle = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumTitle)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomString.make(3))
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumTitle);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Название города - не может быть короче 3 символов")
  void citiShouldBeRequired() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomString.make(2))
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Город не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 255 символами в поле 'Укажите город'")
  void citiShouldBeMaxLength() {
    final String museumTitle = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumTitle)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomString.make(255))
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumTitle);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Название города - не может быть длине 255 символов")
  void citiShouldBeNotLong() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomString.make(256))
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Город не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("О музее - не может быть короче 10 символов")
  void descriptionShouldBeRequired() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setCountry(Country.random().getCountry())
        .setDescription(RandomString.make(9))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Описание не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 10 символами в поле 'О музее'")
  void descriptionShouldBeMinLength() {
    final String museumName = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumName)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomString.make(10))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("О музее - не может быть длине 2000 символов")
  void descriptionShouldBeNotLong() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setCountry(Country.random().getCountry())
        .setDescription(RandomString.make(2001))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Описание не может быть длиннее 2000 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Музей создается с 2000 символами в поле 'О музее'")
  void descriptionShouldBeBigLength() {
    final String museumName = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumName)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomString.make(2000))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Добавлен музей: " + museumName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Страна - обязательна для добавления музея")
  void countryRequiredToAddMuseum() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .checkThatComponentLoaded();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение музея - обязательна для добавления музея")
  void photoRequiredToAddMuseum() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(RandomDataUtils.museum())
        .setDescription(RandomDataUtils.shortBio())
        .setCountry(Country.random().getCountry())
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .checkThatComponentLoaded();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Пользователь может закрыть форму создания музея без добавления музея")
  void userCanCloseTheMuseumCreationForm() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .clickButtonCloseForm(MuseumPage.class)
        .checkThatPageLoaded();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение музея - загрузка файла невалидного формата")
  void uploadingAnInvalidFileFormat() {
    final String museumName = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumName)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/error-file.torrent")
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumPage.class)
        .checkAlert("Изображение музея: Фото должно начинаться с 'data:image/'");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated() {
    final String museumName = RandomDataUtils.museum();
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .clickAddMuseumButton()
        .checkThatComponentLoaded()
        .setTitle(museumName)
        .setCountry(Country.random().getCountry())
        .setDescription(RandomDataUtils.shortBio())
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/oversize.png")
        .setCity(RandomDataUtils.city())
        .clickButtonAddMuseum(MuseumForm.class)
        .assertTitleRequired("Максимальный размер изображения 1 Mb");
  }
}

