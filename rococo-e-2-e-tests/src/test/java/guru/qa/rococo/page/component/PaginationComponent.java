package guru.qa.rococo.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

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
    (gridList.get(gridList.size() - 1)).scrollIntoView(true);
    gridList.shouldHave(sizeGreaterThan(initialSize), Duration.ofSeconds(10));
  }
}
