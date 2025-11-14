package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

@WebTest
@ParametersAreNonnullByDefault
@DisplayName("Тесты на профиль")
public class ProfileTest {

  @User(avatar = "avatar.jpg")
  @ApiLogin
  @ScreenShotTest(expected = "profile/avatar-modal.png")
  @DisplayName("Фото аватара отображается в профиле")
  void avatarPhotoIsDisplayedInTheProfile(BufferedImage image) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickSwitchIsWhiteTheme(false, Header.class)
        .openProfile()
        .checkThatFormLoaded()
        .checkImgAvatar(image);
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Пользователь может выйти из учетной записи")
  void userCanLogOutOfTheAccount() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkThatFormLoaded()
        .clickButtonExitProfile(MainPage.class)
        .checkThatPageLoaded()
        .checkAlert("Сессия завершена")
        .getHeader()
        .assertUnauthorized();
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("Пользователь может закрыть форму профиля без изменений")
  void userCanCloseTheProfileFormWithoutAnyChanges() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .openProfile()
        .checkThatFormLoaded()
        .clickButtonClosedProfile(MainPage.class)
        .checkThatPageLoaded()
        .getHeader()
        .assertAuthorized();
  }

  @User(avatar = "avatar.jpg")
  @ApiLogin
  @ScreenShotTest(expected = "profile/update-avatar.png")
  @DisplayName("Пользователь может обновить данные в профиле")
  void userCanUpdateTheProfileData(BufferedImage image, UserJson user) {
    final String userNike = user.username();
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickSwitchIsWhiteTheme(false, Header.class)
        .openProfile()
        .checkThatFormLoaded()
        .updateProfile(
            MainPage.class,
            Config.getInstance().imageContentBaseDir() + "/avatar-update.jpg",
            RandomDataUtils.randomUsername(),
            RandomDataUtils.randomSurname())
        .checkThatPageLoaded()
        .getHeader()
        .assertAuthorized()
        .openProfile()
        .checkThatFormLoaded()
        .checkNick(userNike)
        .checkImgAvatar(image);
  }


  @User
  @ApiLogin
  @Test
  @DisplayName("Изображение аватара - загрузка файла невалидного формата")
  void uploadingAnInvalidFileFormat() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickSwitchIsWhiteTheme(false, Header.class)
        .openProfile()
        .checkThatFormLoaded()
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/error-file.torrent")
        .clickButtonUpdateProfile(MainPage.class)
        .checkAlert("photo: Фото должно начинаться с 'data:image/'");
  }

  @User
  @ApiLogin
  @Test
  @DisplayName("При загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickSwitchIsWhiteTheme(false, Header.class)
        .openProfile()
        .checkThatFormLoaded()
        .setPhoto(Config.getInstance().imageContentBaseDir() + "/oversize.png")
        .clickButtonUpdateProfile(MainPage.class)
        .checkAlert("avatar: Размер аватара не должен превышать 1MB");
  }


}
