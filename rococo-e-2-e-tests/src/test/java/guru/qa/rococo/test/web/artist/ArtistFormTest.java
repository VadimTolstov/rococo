package guru.qa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.Country;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.detail.ArtistDetailPage;
import guru.qa.rococo.page.form.ArtistForm;
import guru.qa.rococo.page.form.MuseumForm;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на форму создания/редактирования художника")
@ParametersAreNonnullByDefault
public class ArtistFormTest {

  private static final String FOLDER_NAME = "artists";


  @User
  @ApiLogin
  @ScreenShotTest(expected = "artist/artist-detail/picasso.png")
  @DisplayName("Авторизованный пользователь может добавить художника")
  void authorizedUserShouldCanAddArtist(BufferedImage image) {
    final ArtistJson artist = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/picasso.png"
    );

    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .addArtist(artist)
        .checkAlert("Добавлен художник: " + artist.name())
        .openDetailPage(artist.name())
        .checkThatPageLoaded()
        .checkDetailsArtist(artist)
        .checkImage(image);
  }

  @Content(artists = {@Artist(photo = "artists/vangog.png")})
  @User
  @ApiLogin
  @ScreenShotTest(expected = "artist/artist-detail/shishkin.png")
  @DisplayName("Авторизованный пользователь может редактировать Художника")
  void authorizedUserShouldCanEditArtist(ContentJson content, BufferedImage expected) {
    final ArtistJson oldArtist = new ArrayList<>(content.artists()).getFirst();
    final ArtistJson newArtist = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/shishkin.png"
    );

    Selenide.open(ArtistDetailPage.URL + oldArtist.id(), ArtistDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailsArtist(oldArtist)
        .clickEdit()
        .fullUpdateArtist(newArtist)
        .checkAlert("Обновлен художник: " + newArtist.name())
        .checkThatPageLoaded()
        .checkImage(expected);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Имя художника - не может быть короче 3 символов")
  void nameShouldBeRequired() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomString.make(2))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistForm.class)
        .assertTitleRequired("Имя не может быть короче 3 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Художник создается с 3 символами в поле 'Имя'")
  void nameShouldBeMinLength() {
    final String artistName = RandomString.make(3);
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(artistName)
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistPage.class)
        .checkAlert("Добавлен художник: " + artistName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Художник создается с 255 символами в поле 'Имя'")
  void nameShouldBeMaxLength() {
    final String artistName = RandomString.make(255);
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(artistName)
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistPage.class)
        .checkAlert("Добавлен художник: " + artistName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Имя художника - не может быть длине 255 символов")
  void nameShouldBeNotLong() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomString.make(256))
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistForm.class)
        .assertTitleRequired("Имя не может быть длиннее 255 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Артист создается с 10 символами в поле 'Биография'")
  void biographyShouldBeMinLength() {
    final String artistName = RandomDataUtils.randomArtistName();
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(artistName)
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomString.make(10))
        .clickButtonAddArtist(ArtistPage.class)
        .checkAlert("Добавлен художник: " + artistName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Биография художника - не может быть короче 10 символов")
  void biographyShouldBeRequired() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.randomArtistName())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomString.make(9))
        .clickButtonAddArtist(ArtistForm.class)
        .assertTitleRequired("Биография не может быть короче 10 символов");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Художник создается с 2000 символами в поле 'Биография'")
  void biographyShouldBeMaxLength() {
    final String artistName = RandomDataUtils.randomArtistName();
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(artistName)
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomString.make(2000))
        .clickButtonAddArtist(ArtistPage.class)
        .checkAlert("Добавлен художник: " + artistName);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Биография художника - не может быть длине 2000 символов")
  void biographyShouldBeNotLong() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.randomArtistName())
        .setPhoto(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setBiography(RandomString.make(2001))
        .clickButtonAddArtist(ArtistForm.class)
        .assertTitleRequired("Биография не может быть длиннее 2000 символов");
  }


  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение художника - обязательна для добавления художника")
  void photoRequiredToAddArtist() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.randomArtistName())
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistForm.class)
        .checkThatComponentLoaded();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Пользователь может закрыть форму создания художника без добавления художника")
  void userCanCloseTheArtistCreationForm() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .clickButtonCloseForm(ArtistPage.class)
        .checkThatPageLoaded();
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение художника - загрузка файла невалидного формата")
  void uploadingAnInvalidFileFormat() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.randomArtistName())
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/error-file.torrent")
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistPage.class)
        .checkAlert("photo: Фото должно начинаться с 'data:image/'");
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.randomArtistName())
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/oversize.png")
        .setBiography(RandomDataUtils.shortBio())
        .clickButtonAddArtist(ArtistForm.class)
        .assertTitleRequired("Максимальный размер изображения 1 Mb");
  }
}

