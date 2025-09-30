package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.*;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
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

  @NonNull
  @Step("Нажать на логотип")
  public MainPage clickLogo() {
    logo.click();
    return new MainPage();
  }

  @NonNull
  @Step("Нажать на кнопку 'Картины'")
  public PaintingPage clickPaintingsLink() {
    paintingsLink.click();
    return new PaintingPage();
  }

  @NonNull
  @Step("Нажать на кнопку 'Художники'")
  public ArtistPage clickArtistsLink() {
    artistsLink.click();
    return new ArtistPage();
  }

  @NonNull
  @Step("Нажать на кнопку 'Музеи'")
  public MuseumPage clickMuseumsLink() {
    museumsLink.click();
    return new MuseumPage();
  }

  @NonNull
  @Step("Нажать на switch светлая/темная тема")
  public MainPage clickSwitch() {
    lightSwitch.click();
    return new MainPage();
  }

  @NonNull
  @Step("Нажать на кнопку 'Войти'")
  public LoginPage clickLoginButton() {
    loginButton.click();
    return new LoginPage();
  }

  @NonNull
  @Step("Открыть профиль пользователя")
  public ProfileModal openProfile() {
    profileButton.shouldBe(Condition.visible)
        .click();
    return new ProfileModal();
  }

  @NonNull
  @Step("Проверить, что пользователь авторизован")
  public Header assertAuthorized() {
    profileButton.shouldBe(exist);
    return this;
  }

  @NonNull
  @Step("Проверить, что пользователь не авторизован")
  public Header assertUnauthorized() {
    profileButton.shouldBe(not(visible));
    loginButton.shouldBe(visible);
    return this;
  }

  //todo Check avatar image метод для скриншотТеста
}
