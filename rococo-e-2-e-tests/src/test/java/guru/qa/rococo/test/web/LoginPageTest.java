package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

  @Test//todo редерект фронта на http://127.0.0.1:3000/ при попытке перейти на http://127.0.0.1:9000/login
  @DisplayName("Авторизация незарегистрированного пользователя")
  void authorizationOfAnUnregisteredUser() {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .doLogin(new LoginPage(), "Smitty", "133jkdDJF")
        .checkErrorMessage();
  }

  @User
  @Test//todo заменить на страницу регистрации
  @DisplayName("Переход на страницу регистрации")
  void userAuthorizationWithInvalidPassword(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
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
