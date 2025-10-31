package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.ScreenshotConditions;
import guru.qa.rococo.page.*;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

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
  private final SelenideElement lightSwitch = self.$("div.lightswitch-track");
  private final SelenideElement loginButton = self.$("button.btn.variant-filled-primary");
  private final SelenideElement avatarImage = self.$("img.avatar-image, svg.avatar-initials");
  private final SelenideElement profileButton = self.$("figure").parent();
  private final SelenideElement switchWhiteTheme = $("div[aria-checked='true']");


  @NonNull
  @Step("Нажать на логотип")
  public MainPage clickLogo() {
    logo.click();
    return new MainPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Нажать на кнопку 'Картины'")
  public PaintingPage clickPaintingsLink() {
    paintingsLink.click();
    return new PaintingPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Нажать на кнопку 'Художники'")
  public ArtistPage clickArtistsLink() {
    artistsLink.click();
    return new ArtistPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Нажать на кнопку 'Музеи'")
  public MuseumPage clickMuseumsLink() {
    museumsLink.click();
    return new MuseumPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Нажать на switch светлая/темная тема")
  public  <T> T clickSwitchIsWhiteTheme(boolean isWhiteTheme, Class<T> expected) {
    if (isWhiteTheme) {
      if (!switchWhiteTheme.isDisplayed()) {
        lightSwitch.click();
      }
      return toPage(expected);
    }
    if (switchWhiteTheme.isDisplayed()) {
      lightSwitch.click();
    }
    return toPage(expected);
  }

  @NonNull
  @Step("Нажать на кнопку 'Войти'")
  public LoginPage clickLoginButton() {
    loginButton.click();
    return new LoginPage();
  }

  @NonNull
  @Step("Открыть профиль пользователя")
  public Profile openProfile() {
    profileButton.shouldBe(Condition.visible)
        .click();
    return new Profile();
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

  @Step("Проверяем изображение аватара")
  public void checkImgAvatar(BufferedImage expected) {
    avatarImage.shouldBe(ScreenshotConditions.image(expected));
  }
}
