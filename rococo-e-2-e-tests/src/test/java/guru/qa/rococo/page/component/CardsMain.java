package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.PaintingPage;
import io.qameta.allure.Step;
import lombok.NonNull;

import java.time.Duration;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CardsMain extends BaseComponent<CardsMain> {
  public CardsMain() {
    super($("#page"));
  }

  private final SelenideElement paintingsLink = self.$("a[href='/painting']");
  private final SelenideElement artistsLink = self.$("a[href='/artist']");
  private final SelenideElement museumsLink = self.$("a[href='/museum']");
  private final SelenideElement displayMain = self.$$("p").findBy(text("Ваши любимые картины и художники всегда рядом"));

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

  @Step("Проверяем, что главная страница загрузилась")
  public void checkDisplayMain() {
    displayMain.shouldHave(visible, Duration.ofSeconds(10));
  }
  //todo image метод для скриншотТеста проверить отображения картинок
}