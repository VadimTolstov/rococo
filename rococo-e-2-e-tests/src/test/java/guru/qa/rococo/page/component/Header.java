package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.ProfileModal;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

  public Header() {
    super($("#shell-header"));
  }

  private final SelenideElement logo = self.$("h1 a");
  private final SelenideElement paintingsLink = self.$("nav.list-nav a[href='/painting']");
  private final SelenideElement artistsLink = self.$("nav.list-nav a[href='/artist']");
  private final SelenideElement museumsLink = self.$("nav.list-nav a[href='/museum']");
  private final SelenideElement lightSwitch = self.$("div.lightswitch-track]");
  private final SelenideElement loginButton = self.$("button.btn.variant-filled-primary");
  private final SelenideElement avatarImage = self.$("img.avatar-image, svg.avatar-initials");
  private final SelenideElement profileButton = self.$("figure").parent();


  @Step("Нажать на логотип")
  public MainPage clickLogo() {
    logo.click();
    return new MainPage();
  }

  @Step("Нажать на кнопку 'Картины'")
  public MainPage clickPaintingsLink() {
    paintingsLink.click();
    return new MainPage();
  }

  @Step("Нажать на кнопку 'Художники'")
  public MainPage clickArtistsLink() {
    artistsLink.click();
    return new MainPage();
  }

  @Step("Нажать на кнопку 'Музеи'")
  public MainPage clickMuseumsLink() {
    museumsLink.click();
    return new MainPage();
  }

  @Step("Нажать на switch светлая/темная тема")
  public MainPage clickSwitch() {
    lightSwitch.click();
    return new MainPage();
  }

  @Step("Нажать на кнопку 'Войти'")
  public MainPage clickLoginButton() {
    loginButton.click();
    return new MainPage();
  }

  @Step("Открыть профиль пользователя")
  public ProfileModal openProfile() {
    profileButton.shouldBe(Condition.visible)
        .click();
    return new ProfileModal();
  }

  @Step("Проверить, что пользователь авторизован")
  public Header assertAuthorized() {
    profileButton.shouldBe(exist);
    return this;
  }

  @Step("Проверить, что пользователь не авторизован")
  public Header assertUnauthorized(){
    profileButton.shouldBe(not(visible));
    loginButton.shouldBe(visible);
    return this;
  }

  //todo Check avatar image метод для теста
}
