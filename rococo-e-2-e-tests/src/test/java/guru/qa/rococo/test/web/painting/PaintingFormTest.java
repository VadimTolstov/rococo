package guru.qa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.detail.PaintingDetailPage;
import guru.qa.rococo.page.form.PaintingForm;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на форму создания картины")
public class PaintingFormTest {

  private static final String FOLDER_NAME = "paintings";


  @User
  @ApiLogin
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @ScreenShotTest(expected = "painting/painting-detail/ivan-the-terrible.png")
  @DisplayName("Авторизованный пользователь может добавить картину")
  void authorizedUserShouldCanAddPainting(BufferedImage image, ContentJson content) {
    final MuseumJson museum = new ArrayList<>(content.museums()).getFirst();
    final ArtistJson artist = new ArrayList<>(content.artists()).getFirst();
    final PaintingJson painting = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/ivan-the-terrible.png",
        artist,
        museum
    );

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .addPainting(painting)
        .checkAlert("Добавлена картины: " + painting.title())
        .openDetailPage(painting.title())
        .checkThatPageLoaded()
        .checkDetailPainting(painting)
        .checkImage(image);
  }

  @Content(
      museums = @Museum,
      artists = @Artist,
      paintings = @Painting(content = "/paintings/mona-liza.png"),
      museumCount = 1,
      artistCount = 1
  )
  @User
  @ApiLogin
  @ScreenShotTest(expected = "painting/painting-detail/starry-night.png")
  @DisplayName("Авторизованный пользователь может редактировать картину")
  void authorizedUserShouldCanEditPainting(ContentJson content, BufferedImage expected) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    final String oldMuseum = painting.museum().title();
    final String oldArtist = painting.artist().name();

    final ArtistJson newArtist = new ArrayList<>(content.artists())
        .stream()
        .filter(artist -> !oldArtist.equals(artist.name()))
        .toList()
        .getFirst();
    final MuseumJson newMuseum = new ArrayList<>(content.museums())
        .stream()
        .filter(museum -> !oldMuseum.equals(museum.title()))
        .toList()
        .getFirst();

    final PaintingJson newPainting = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        Config.getInstance().imageContentBaseDir() + FOLDER_NAME + "/starry-night.jpg",
        newArtist,
        newMuseum
    );

    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailPainting(painting)
        .clickEdit()
        .fullUpdatePainting(newPainting)
        .checkAlert("Обновлена картина: " + newPainting.title())
        .checkDetailPainting(newPainting)
        .checkImage(expected);
  }

  @Content(paintingCount = 1)
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonUpdatePainting(ContentJson content) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdatePaintingButton();
  }

  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Название картины - не может быть короче 3 символов")
  void titleShouldBeRequired(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomString.make(2))
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Название не может быть короче 3 символов");
  }

  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Картина создается с 3 символами в поле 'Название картины'")
  void titleShouldBeMinLength(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String paintingName = RandomString.make(3);

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(paintingName)
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingDetailPage.class)
        .checkAlert("Добавлена картины: " + paintingName);
  }


  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Картина создается с 255 символами в поле 'Название картины'")
  void titleShouldBeMaxLength(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String paintingName = RandomString.make(255);

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(paintingName)
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingPage.class)
        .checkAlert("Добавлена картины: " + paintingName);
  }

  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Название картины - не может быть длине 255 символов")
  void titleShouldBeNotLong(ContentJson content) {

    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String paintingName = RandomString.make(256);

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(paintingName)
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Название не может быть длиннее 255 символов");
  }

  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Описание картины - не может быть короче 10 символов")
  void descriptionShouldBeRequired(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomString.make(9))
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Описание не может быть короче 10 символов");
  }

  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Картина создается с 10 символами в поле 'Описание картины'")
  void descriptionShouldBeMinLength(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String paintingName = RandomDataUtils.painting();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(paintingName)
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomString.make(10))
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingPage.class)
        .checkAlert("Добавлена картины: " + paintingName);
  }


  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Описание картины - не может быть длине 2000 символов")
  void descriptionShouldBeNotLong(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomString.make(2001))
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Описание не может быть длиннее 2000 символов");
  }


  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Картина создается с 2000 символами в поле  'Описание картины'")
  void descriptionShouldBeBigLength(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String paintingName = RandomDataUtils.painting();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(paintingName)
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomString.make(2000))
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingPage.class)
        .checkAlert("Добавлена картины: " + paintingName);
  }

  @Content(
      museumCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Художник - обязателен для добавления картины")
  void artistRequiredToAddPainting(ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setDescription(RandomDataUtils.shortBio())
        .setMuseum(museumName)
        .clickButtonAddPainting(PaintingForm.class)
        .checkThatComponentLoaded();
  }

  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение картины - обязателен для добавления картины")
  void contentRequiredToAddPainting(ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setArtist(artistName)
        .setMuseum(museumName)
        .setDescription(RandomDataUtils.shortBio())
        .clickButtonAddPainting(PaintingForm.class)
        .checkThatComponentLoaded();
  }

  @Content(
      artistCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Музей - обязателен для добавления картины")
  void museumRequiredToAddPainting(ContentJson content) {
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(RandomDataUtils.randomFilePath(FOLDER_NAME))
        .setArtist(artistName)
        .setDescription(RandomDataUtils.shortBio())
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Укажите, где хранится оригинал картины");
  }


  @Test
  @User
  @ApiLogin
  @DisplayName("Пользователь может закрыть форму создания картины без добавления картины")
  void userCanCloseThePaintingCreationForm() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .clickButtonCloseForm(PaintingPage.class)
        .checkThatPageLoaded();
  }

  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("Изображение картины - загрузка файла невалидного формата")
  void uploadingAnInvalidFileFormat(ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(Config.getInstance().imageContentBaseDir() + "/error-file.torrent")
        .setArtist(artistName)
        .setMuseum(museumName)
        .setDescription(RandomDataUtils.shortBio())
        .clickButtonAddPainting(PaintingPage.class)
        .checkAlert("content: Изображение картины должно начинаться с 'data:image/'");

  }

  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @Test
  @User
  @ApiLogin
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(ContentJson content) {
    final String museumName = new ArrayList<>(content.museums()).getFirst().title();
    final String artistName = new ArrayList<>(content.artists()).getFirst().name();

    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .clickAddPaintingButton()
        .checkThatComponentLoaded()
        .setName(RandomDataUtils.painting())
        .setContent(Config.getInstance().imageContentBaseDir() + "/oversize.png")
        .setArtist(artistName)
        .setMuseum(museumName)
        .setDescription(RandomDataUtils.shortBio())
        .clickButtonAddPainting(PaintingForm.class)
        .assertTitleRequired("Максимальный размер изображения 1 Mb");

  }
}
