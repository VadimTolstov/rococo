package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import guru.qa.rococo.page.detail.PaintingDetailPage;
import guru.qa.rococo.page.form.PaintingForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PaintingPage extends BasePage<PaintingPage> {
  public static final String URL = CFG.frontUrl() + "/painting";

  private final SelenideElement pageContainer = $("#page");
  private final SelenideElement title = pageContainer.$$("h2").findBy(text("Картины"));
  private final ElementsCollection paintings = pageContainer.$$(".grid li");
  private final SelenideElement addPaintingButton = pageContainer.$$("button").findBy(Condition.exactText("Добавить картину"));
  @Getter
  protected final Header header = new Header();
  @Getter
  protected final SearchField searchField = new SearchField();

  @NonNull
  @Step("Проверяем, что загрузилась страница с картинами")
  @Override
  public PaintingPage checkThatPageLoaded() {
    title.shouldBe(visible, Duration.ofSeconds(10))
        .shouldHave(exactText("Картины"));
    searchField.getSelf().shouldBe(visible);
    return this;
  }

  @NonNull
  @Step("Найти картину через поиск по тексту {query} и перейти в ее описание по названию {paintingName}")
  public PaintingDetailPage searchAndOpenPainting(String query, String paintingName) {
    searchField.searchThroughButton(query);
    return openDetailPage(paintingName);
  }

  @NonNull
  @Step("Кликнуть по картине {name} и перейти в ее описание")
  public PaintingDetailPage openDetailPage(String name) {
    paintings.findBy(exactText(name))
        .shouldBe(visible.because("Картина '" + name + "' не видна на странице"))
        .shouldBe(interactable.because("Картина '" + name + "' не доступна для клика"))
        .click();
    return new PaintingDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Сравниваем изображение на странице 'Картины'")
  public PaintingPage checkImages(BufferedImage... images) {
    compareImages(paintings.stream().toList(), images);
    return this;
  }

  @NonNull
  @Step("Нажать на кнопку 'Добавить картину'")
  public PaintingForm clickAddPaintingButton() {
    addPaintingButton.shouldBe(visible).click();
    return new PaintingForm();
  }
}
