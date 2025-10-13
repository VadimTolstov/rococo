package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.RegisterPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterPageTest {

//  @Test
//  @DisplayName("Регистрация нового пользователя")
//  void successRegistration() {
//    Selenide.open(MainPage.URL, MainPage.class)
//        .getHeader()
//        .clickLoginButton()
//        .doLogin(new MainPage(), user.username(), user.password())
//        .checkThatPageLoaded();
//  }

  @User
  @Test
  @DisplayName("Регистрация пользователя с зарегистрированным логином")
  void userAuthorization(UserJson user) {
    String as = """
        """;
    Selenide.open(RegisterPage.URL, RegisterPage.class)
        .doRegister(new RegisterPage(), user.username(), user.password())
        .checkErrorMessage(String.format("Username `%s` already exists", user.username()));
  }

  @Test
  @DisplayName("Авторизация незарегистрированного пользователя")
  void authorizationOfAnUnregisteredUser() {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .doLogin(new LoginPage(), "Smitty", "133jkdDJF")
        .checkErrorMessage();
  }

  @User
  @Test//todo заменить на страницу регистрации
  @DisplayName("Переход на страницу регистрации")
  void userAuthorizationWithInvalidPassword(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .clickLoginButton()
        .clickHrefRegisterPage(new LoginPage());
  }

  @Test//todo
  @DisplayName("Отключение маскирование у Password")
  void disablingPasswordMasking() {

  }

  @Test//todo скриншот тест картинки
  @DisplayName("Сравнить изображение на странице LoginPage")
  void disablingPasswordMasking2() {

  }
}
