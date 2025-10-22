package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.ScreenshotConditions;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
  public static final String URL = CFG.frontUrl();

  private final SelenideElement pageContainer = $("#page");
  private final SelenideElement paintingsLink = pageContainer.$("a[href='/painting']");
  private final SelenideElement artistsLink = pageContainer.$("a[href='/artist']");
  private final SelenideElement museumsLink = pageContainer.$("a[href='/museum']");
  private final SelenideElement title = pageContainer.$$("p").findBy(text("Ваши любимые картины и художники всегда рядом"));
  private final SelenideElement root = $("body");
  private final SelenideElement switchWhiteTheme = $("div[aria-checked='true']");

  @Getter
  protected final Header header = new Header();

  @NonNull
  @Step("Проверяем, что загрузилась главная страница")
  @Override
  public MainPage checkThatPageLoaded() {
    header.getSelf().shouldHave(visible, Duration.ofSeconds(10))
        .shouldHave(Condition.text("Ro"))
        .shouldHave(Condition.text("coco"));
    paintingsLink.shouldBe(visible).shouldBe(clickable);
    artistsLink.shouldBe(visible).shouldBe(clickable);
    museumsLink.shouldBe(visible).shouldBe(clickable);
    title.shouldBe(visible).shouldHave(exactText("Ваши любимые картины и художники всегда рядом"));
    return this;
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
  @Step("Сравниваем изображение на главной странице")
  public MainPage checkImages(BufferedImage... images) {
    compareImages(List.of(paintingsLink, artistsLink, museumsLink), images);
    return this;
  }

  @Step("Сравниваем изображение картин на главной странице")
  public void checkImagesPainting(BufferedImage image) {
    compareImage(paintingsLink, image);
  }

  @Step("Сравниваем изображение художников на главной странице")
  public void checkImagesArtist(BufferedImage image) {
    compareImage(artistsLink, image);
  }

  @Step("Сравниваем изображение музеев на главной странице")
  public void checkImagesMuseums(BufferedImage image) {
    compareImage(museumsLink, image);
  }

  @Step("Проверка темной темы")
  public void checkDarkTheme(BufferedImage expected) {
    header.clickSwitchIsWhiteTheme(false, MainPage.class);
    root.shouldHave(ScreenshotConditions.image(expected));
  }

  @Step("Проверка светлой темы")
  public void checkLightTheme(BufferedImage expected) {
    header.clickSwitchIsWhiteTheme(true, MainPage.class);
    root.shouldHave(ScreenshotConditions.image(expected));
  }
}
