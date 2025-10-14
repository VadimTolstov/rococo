package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.RegisterPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты страницы логина")
public class LoginPageTest {

  @User
  @Test
  @DisplayName("Авторизация пользователя")
  void userAuthorization(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new MainPage(), user.username(), user.password())
        .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Авторизация незарегистрированного пользователя")
  void authorizationOfAnUnregisteredUser() {
    final String username = RandomDataUtils.randomUsername();
    final String password = RandomDataUtils.randomPassword();
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new LoginPage(), username, password)
        .checkErrorMessage();
  }

  @Test
  @DisplayName("Переход на страницу регистрации по ссылке 'Зарегистрироваться'.")
  void goToTheRegistrationPage() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .clickHrefRegisterPage(new RegisterPage());
  }

  @Test
  @DisplayName("Отключение маскирования у Password")
  void disablingPasswordMasking() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .setPassword("123456")
        .checkPassword();
  }

  @ScreenShotTest(expected = "login/hermitage.png")
  @DisplayName("На странице логина есть изображение эрмитажа")
  void thereIsAnImageOfTheHermitageOnTheLoginPage(BufferedImage expected) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .checkImages(expected);
  }

  @Test
  @DisplayName("Если 'Имя пользователя' пустое, то отображается ошибка")
  void whenUsernameIsEmptyThenErrorNotification() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new LoginPage(), " ", "123456")
        .checkErrorMessage();
  }

  @Test
  @User
  @DisplayName("Если пароль пустой, то отображается ошибка")
  void whenPasswordIsEmptyThenErrorNotification(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new LoginPage(), user.username(), " ")
        .checkErrorMessage();
  }

  @Test
  @User
  @DisplayName("Если пароль неверный, то отображается ошибка")
  void whenPasswordIsIncorrectThenErrorNotification(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new LoginPage(), user.username(), "dsfedds24323")
        .checkErrorMessage();
  }
}
