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
public class LoginPage extends BasePage<LoginPage> {
  public static final String URL = CFG.authUrl() + "login";

  private final SelenideElement pageContainer = $(".content");
  private final SelenideElement inputUserName = pageContainer.$("input[name='username']");
  private final SelenideElement inputPassword = pageContainer.$("input[name='password']");
  private final SelenideElement buttonComeIn = pageContainer.$("button[class='form__submit']");
  private final SelenideElement title = pageContainer.$(".form__header");
  private final SelenideElement errorMessageElement = pageContainer.$(".form__error");
  private final SelenideElement hrefRegisterPage = pageContainer.$("[href='/register']");
  private final String errorMessage = "Неверные учетные данные пользователя";

  @NonNull
  @Step("Проверяем, что загрузилась страница авторизации")
  @Override
  public LoginPage checkThatPageLoaded() {
    pageContainer.shouldHave(visible, Duration.ofSeconds(10));
    title.shouldHave(text("Ro"))
        .$("span").shouldHave(text("coco"));
    return this;
  }

  @NonNull
  @Step("Авторизоваться пользователем с именем {userName} и паролем {password}")
  public <T extends BasePage<?>> T doLogin(T expectedPage, String userName, String password) {
    return setUserName(userName)
        .setPassword(password)
        .clickComeIn(expectedPage);
  }

  @NonNull
  @Step("Заполняем поле 'Имя пользователя'  {userName}")
  public LoginPage setUserName(String userName) {
    inputUserName.setValue(userName);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Пароль'  {password}")
  public LoginPage setPassword(String password) {
    inputPassword.setValue(password);
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение на странице авторизации.")
  public LoginPage checkImages(BufferedImage images) {
    compareImage(pageContainer, images);
    return this;
  }

  @NonNull
  @Step("Кликнуть по кнопке 'Войти'.")
  public <T extends BasePage<?>> T clickComeIn(T expectedPage) {
    buttonComeIn.shouldBe(visible).click();
    return expectedPage;
  }

  @NonNull
  @Step("Проверяем сообщение об ошибке")
  public LoginPage checkErrorMessage() {
    checkErrorMessage(errorMessageElement, errorMessage);
    return this;
  }

  @NonNull
  @Step("Кликнуть по ссылке 'Зарегистрироваться'.")
  public <T extends BasePage<?>> T clickHrefRegisterPage(T expectedPage) {
    hrefRegisterPage.shouldBe(visible).click();
    return expectedPage;
  }
}