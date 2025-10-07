package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.detail.MuseumDetailPage;
import guru.qa.rococo.page.form.MuseumForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MuseumPage extends BasePage<MuseumPage> {
  public static final String URL = CFG.frontUrl() + "/museum";

  private final SelenideElement pageContainer = $("#page");
  private final SelenideElement title = pageContainer.$$("h2").findBy(exactText("Музеи"));
  private final ElementsCollection museums = pageContainer.$$(".grid li");
  private final SelenideElement addMuseumButton = pageContainer.$$("button").findBy(Condition.exactText("Добавить музей"));

  @Getter
  protected final Header header = new Header();
  @Getter
  protected final SearchField searchField = new SearchField();

  @NonNull
  @Step("Проверяем, что загрузилась страница с музеями.")
  @Override
  public MuseumPage checkThatPageLoaded() {
    title.shouldBe(visible, Duration.ofSeconds(10));
    searchField.getSelf().shouldBe(visible);
    return this;
  }

  @NonNull
  @Step("Найти музей через поиск по тексту {museumName} и перейти в ее описание.")
  public MuseumDetailPage searchAndOpenPainting(String museumName) {
    searchField.searchThroughButton(museumName);
    return openDetailPage(museumName);
  }

  @NonNull
  @Step("Кликнуть по музею {name} и перейти в его описание.")
  public MuseumDetailPage openDetailPage(String name) {
    final SelenideElement element = museums.findBy(exactText(name));
    scrollToElement(element);
    element.shouldBe(visible.because("Музей '" + name + "' не виден на странице"))
        .shouldBe(interactable.because("Музей '" + name + "' не доступен для клика"))
        .click();
    return new MuseumDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Сравниваем изображение на странице 'Музеи'.")
  public MuseumPage checkImages(BufferedImage... images) {
    compareImages(museums.stream().toList(), images);
    return this;
  }

  @NonNull
  @Step("Нажать на кнопку 'Добавить музей'.")
  public MuseumForm clickAddPaintingButton() {
    addMuseumButton.shouldBe(visible).click();
    return new MuseumForm().checkThatComponentLoaded();
  }
}
