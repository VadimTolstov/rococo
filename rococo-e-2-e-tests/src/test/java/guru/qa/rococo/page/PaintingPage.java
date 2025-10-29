package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.NotFoundComponent;
import guru.qa.rococo.page.component.PaginationComponent;
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
  public static final String URL = CFG.frontUrl() + "painting";

  private final SelenideElement pageContainer = $("#page");
  private final SelenideElement title = pageContainer.$$("h2").findBy(exactText("Картины"));
  private final ElementsCollection paintings = pageContainer.$$(".grid li");
  private final SelenideElement addPaintingButton = pageContainer.$$("button").findBy(Condition.exactText("Добавить картину"));
  @Getter
  protected final Header header = new Header();
  @Getter
  protected final SearchField searchField = new SearchField();
  @Getter
  protected NotFoundComponent notFoundComponent = new NotFoundComponent();
  @Getter
  protected final PaginationComponent paginationComponent = new PaginationComponent();

  @NonNull
  @Step("Проверяем, что загрузилась страница с картинами.")
  @Override
  public PaintingPage checkThatPageLoaded() {
    title.shouldBe(visible, Duration.ofSeconds(10));
    searchField.getSelf().shouldBe(visible);
    return this;
  }

  @NonNull
  @Step("Найти картину через поиск по тексту {paintingName} и перейти в ее описание.")
  public PaintingDetailPage searchAndOpenPainting(String paintingName) {
    searchField.searchThroughButton(paintingName);
    return openDetailPage(paintingName);
  }

  @NonNull
  @Step("Кликнуть по картине {name} и перейти в ее описание.")
  public PaintingDetailPage openDetailPage(String name) {
    final SelenideElement element = paintings.findBy(exactText(name));
    scrollToElement(element);
    element.shouldBe(visible.because("Картина '" + name + "' не видна на странице"))
        .shouldBe(interactable.because("Картина '" + name + "' не доступна для клика"))
        .click();
    return new PaintingDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Сравниваем изображение на странице 'Картины'.")
  public PaintingPage checkImages(BufferedImage... images) {
    compareImages(paintings.stream().toList(), images);
    return this;
  }

  @Step("Сравниваем изображение на странице 'Картины'.")
  public void checkImage(BufferedImage images, String paintingName) {
    compareImage(pageContainer.$(Selectors.byText(paintingName)).parent(), images);
  }

  @NonNull
  @Step("Нажать на кнопку 'Добавить картину'.")
  public PaintingForm clickAddPaintingButton() {
    addPaintingButton.shouldBe(visible).click();
    return new PaintingForm().checkThatComponentLoaded();
  }

  @Step("Проверяем, что у неавторизованного пользователя кнопка 'Добавить катину' не отображается.")
  public void checkNoAddPaintingButton() {
    addPaintingButton.shouldNot(exist);
  }

  @Step("Проверяем отображение текста, когда картина не найден")
  public void checkMessagePaintingNotFound() {
    notFoundComponent.shouldShown("Картины не найдены",
        "Для указанного вами фильтра мы не смогли не найти ни одной картины");
  }

  @Step("Проверяем, что при пустом списке картин отображается текст")
  public void checkMessagePaintingEmpty() {
    pageContainer.shouldHave(Condition.visible)
        .shouldHave(Condition.text("Пока что список картин пуст. Чтобы пополнить коллекцию, добавьте новую картину"));
  }
}
