package guru.qa.rococo.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;

public class PaginationComponent extends BaseComponent<PaginationComponent> {
  public PaginationComponent() {
    super($("#page"));
  }

  private final ElementsCollection gridList = getSelf().$$(".grid li");


  @Step("Проверяем работу пагинации")
  public void checkingThePagination() {
    gridList.shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    final int initialSize = gridList.size();
    gridList.get(gridList.size() - 1).scrollIntoView(true);
    gridList.shouldHave(sizeGreaterThan(initialSize), Duration.ofSeconds(10));
  }

  @Step("Ищем элемент в пагинации по названию {optionText}")
  public SelenideElement scrollPagination(ElementsCollection elementsCollection, String optionText) {
    elementsCollection.shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    int collectionSize;

    for (int i = 0; i < 15; i++) {
      collectionSize = elementsCollection.size();

      var targetOption = elementsCollection
          .stream()
          .filter(element ->
              StringUtils.equalsAnyIgnoreCase(optionText, element.getText()))
          .findFirst();

      if (targetOption.isPresent()) {
        return targetOption.get();
      }

      elementsCollection.last().scrollIntoView(true);

      try {
        elementsCollection.shouldBe(CollectionCondition.sizeGreaterThan(collectionSize), Duration.ofSeconds(5));
      } catch (AssertionError ignored) {
      }
    }

    throw new AssertionError("Элемент не найден: " + optionText);
  }
}
