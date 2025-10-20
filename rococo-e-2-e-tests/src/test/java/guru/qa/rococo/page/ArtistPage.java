package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.detail.ArtistDetailPage;
import guru.qa.rococo.page.form.ArtistForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistPage extends BasePage<ArtistPage> {
  public static final String URL = CFG.frontUrl() + "artist";

  private final SelenideElement pageContainer = $("#page");
  private final SelenideElement title = pageContainer.$("h2");
  private final ElementsCollection artist = pageContainer.$$(".grid li");
  private final SelenideElement addArtistButton = pageContainer.$("button[class='btn variant-filled-primary ml-4']");


  @Getter
  protected final Header header = new Header();
  @Getter
  protected final SearchField searchField = new SearchField();

  @NonNull
  @Step("Проверяем, что загрузилась страница с художниками.")
  @Override
  public ArtistPage checkThatPageLoaded() {
    title.shouldBe(visible, Duration.ofSeconds(10));
    searchField.getSelf().shouldBe(visible);
    return this;
  }

  @NonNull
  @Step("Найти художника через поиск по тексту {artistName} и перейти в его описание.")
  public ArtistDetailPage searchAndOpenPainting(String artistName) {
    searchField.searchThroughButton(artistName);
    return openDetailPage(artistName);
  }

  @NonNull
  @Step("Кликнуть по художнику {name} и перейти в его описание.")
  public ArtistDetailPage openDetailPage(String name) {
    final SelenideElement element = artist.findBy(exactText(name));
    scrollToElement(element);
    element.shouldBe(visible.because("Художник '" + name + "' не видна на странице"))
        .shouldBe(interactable.because("Художник '" + name + "' не доступен для клика"))
        .click();
    return new ArtistDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Сравниваем изображение на странице 'Художники'.")
  public ArtistPage checkImages(BufferedImage... images) {
    compareImages(artist.stream().toList(), images);
    return this;
  }

  @NonNull
  @Step("Нажать на кнопку 'Добавить художника'.")
  public ArtistForm clickAddPaintingButton() {
    addArtistButton.shouldBe(visible).click();
    return new ArtistForm().checkThatComponentLoaded();
  }

  @Step("Проверяем, что кнопки 'Добавить художника' нет.")
  public void checkNoAddPaintingButton() {
    addArtistButton.shouldNot(exist);
  }
}
