package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.RegisterPage;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
@DisplayName("Тесты страницы регистрации")
public class RegisterPageTest {

  @Test
  @DisplayName("Регистрация и авторизация нового пользователя")
  void successRegistration() {
    final String username = RandomDataUtils.randomUsername();
    final String password = RandomDataUtils.randomPassword();
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegisterAndToMainPage(username, password)
        .doLogin(new MainPage(), username, password)
        .checkThatPageLoaded();
  }

  @User
  @Test
  @DisplayName("При регистрации с занятым пользователем должна отображаться ошибка")
  void registeringAnExistingUser(UserJson user) {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(user.username(), user.password())
        .checkErrorMessage(String.format("Username `%s` already exists", user.username()));
  }

  @Test
  @DisplayName("Переход на страницу авторизации по кнопке 'Войти'.")
  void goToAuthorizationPage() {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkThatPageLoaded();
  }

  @ScreenShotTest(expected = "register/renuar.png")
  @DisplayName("Сравнить изображение на странице RegisterPage")
  void compareTheImageOnTheRegisterPage(BufferedImage expected) {
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .checkImages(expected);
  }

  @Test
  @DisplayName("Имя пользователя, пароль и повторение пароля не должны содержать одни пробелы")
  void whenUsernamePasswordAndPasswordSubmitIsEmptyThenErrorNotification() {
    final String spaces = "       ";
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(spaces, spaces)
        .checkErrorMessage(
            "Username can not be blank",
            "Password can not be blank",
            "Password submit can not be blank");
  }

  @Test
  @DisplayName("Имя пользователя должен быть больше 2 символов")
  void usernameShouldBeGraterThan2Characters() {
    final String username = RandomString.make(2);
    final String password = RandomDataUtils.randomPassword();
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(username, password)
        .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @DisplayName("Имя пользователя должен быть меньше 51 символа")
  void usernameShouldBeLessThan51Characters() {
    final String username = RandomString.make(51);
    final String password = RandomDataUtils.randomPassword();
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(username, password)
        .checkErrorMessage("Allowed username length should be from 3 to 50 characters");
  }

  @Test
  @DisplayName("Пароль должен быть больше 2 символов")
  void passwordShouldBeGraterThan2Characters() {
    final String passErrorText = "Allowed password length should be from 3 to 12 characters";
    final String username = RandomDataUtils.randomUsername();
    final String password = RandomString.make(2);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(username, password)
        .checkErrorMessage(passErrorText, passErrorText);
  }

  @Test
  @DisplayName("Пароль должен быть меньше 13 символов")
  void passwordShouldBeLessThan13Characters() {
    final String passErrorText = "Allowed password length should be from 3 to 12 characters";
    final String username = RandomDataUtils.randomUsername();
    final String password = RandomString.make(13);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(username, password)
        .checkErrorMessage(passErrorText, passErrorText);
  }

  @Test
  @DisplayName("Значение полей 'пароль' и 'повторите пароль' должно быть одинаковым")
  void passwordSubmitShouldBeEqualToPassword() {
    final String username = RandomDataUtils.randomUsername();
    final String password = RandomString.make(12);
    final String passwordSubmit = RandomString.make(12);
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .setUserName(username)
        .setPassword(password)
        .setPasswordSubmit(passwordSubmit)
        .clickComeIn()
        .checkErrorMessage("Passwords should be equal");
  }
}
