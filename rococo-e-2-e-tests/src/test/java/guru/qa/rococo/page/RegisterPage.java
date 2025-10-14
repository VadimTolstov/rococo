package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {
  public static final String URL = CFG.authUrl() + "register";

  private final SelenideElement pageContainer = $(".content");
  private final SelenideElement inputUserName = pageContainer.$("input[name='username']");
  private final SelenideElement inputPassword = pageContainer.$("input[name='password']");
  private final SelenideElement inputPasswordSubmit = pageContainer.$("input[name='passwordSubmit']");
  private final SelenideElement hrefLoginPage = pageContainer.$("[href='login']");
  private final SelenideElement title = pageContainer.$(".form__header");
  private final SelenideElement errorMessageElement = pageContainer.$(".form__error");
  private final SelenideElement buttonRegister = pageContainer.$("button[class='form__submit']");
  private final SelenideElement buttonRedirectMainPage = pageContainer.$("a.form__submit");
  private final SelenideElement messageSuccessfulRegistration = pageContainer.$(".form__subheader");
  private final String errorMessage = "Неверные учетные данные пользователя";
  private final String messageRegistration = "Добро пожаловать в Ro";


  @NonNull
  @Step("Проверяем, что загрузилась страница регистрации")
  @Override
  public RegisterPage checkThatPageLoaded() {
    pageContainer.shouldHave(visible, Duration.ofSeconds(10));
    title.shouldHave(text("Ro"))
        .$("span").shouldHave(text("coco"));
    return this;
  }

  @NonNull
  @Step("Регистрация пользователем с именем {userName} и паролем {password}")
  public RegisterPage doRegister(String userName, String password) {
    return setUserName(userName)
        .setPassword(password)
        .setPasswordSubmit(password)
        .clickComeIn();
  }

  @NonNull
  @Step("Регистрация пользователем с именем {userName} и паролем {password} и переход на страницу MainPage")
  public LoginPage doRegisterAndToMainPage(String userName, String password) {
    return doRegister(userName, password)
        .checkMessageSuccessfulRegistration()
        .clickButtonToMainPAge();
  }

  @NonNull
  @Step("Заполняем поле 'Имя пользователя'  {userName}")
  public RegisterPage setUserName(String userName) {
    inputUserName.setValue(userName);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Пароль' {password}")
  public RegisterPage setPassword(String password) {
    inputPassword.setValue(password);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Пароль' {password}")
  public RegisterPage setPasswordSubmit(String password) {
    inputPasswordSubmit.setValue(password);
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение на странице регистрации.")
  public RegisterPage checkImages(BufferedImage images) {
    compareImage(pageContainer, images);
    return this;
  }

  @NonNull
  @Step("Кликнуть по кнопке 'Войти'.")
  public RegisterPage clickComeIn() {
    buttonRegister.shouldBe(visible).click();
    return this;
  }

  @NonNull
  @Step("Кликнуть по ссылке 'Войти'.")
  public LoginPage clickHrefLoginPage() {
    hrefLoginPage.shouldBe(visible).click();
    return new LoginPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Проверяем, сообщение об успешной регистрации.")
  public RegisterPage checkMessageSuccessfulRegistration() {
    messageSuccessfulRegistration
        .shouldBe(visible)
        .shouldHave(exactOwnText(messageRegistration));
    return this;
  }

  @NonNull
  @Step("Кликнуть по кнопке 'Войти в систему'.")
  public LoginPage clickButtonToMainPAge() {
    buttonRedirectMainPage.shouldBe(visible).click();
    return new LoginPage().checkThatPageLoaded();
  }
}
