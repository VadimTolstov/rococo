package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
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
  private final String errorMessage = "Неверные учетные данные пользователя";


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
  public <T extends BasePage<?>> T doRegister(T expectedPage, String userName, String password) {
    return setUserName(userName)
        .setPassword(password)
        .setPasswordSubmit(password)
        .clickComeIn(expectedPage);
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
  public <T extends BasePage<?>> T clickComeIn(T expectedPage) {
    buttonRegister.shouldBe(visible).click();
    return expectedPage;
  }

  @NonNull
  @Step("Кликнуть по ссылке 'Войти'.")
  public <T extends BasePage<?>> T clickHrefLoginPage(T expectedPage) {
    hrefLoginPage.shouldBe(visible).click();
    return expectedPage;
  }
}
