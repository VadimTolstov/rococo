package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {
  public SearchField() {
    super($("input[title='Искать картины...']"));
  }

  private final SelenideElement searchButton = self.parent().$("img[alt='Иконка поиска']");

  @NonNull
  @Step("Ищем поиском через Enter {query}")
  public SearchField searchThroughEnter (String query) {
    self.shouldBe(visible).setValue(query).pressEnter();
    return this;
  }

  @NonNull
  @Step("Ищем поиском через кнопку поиска {query}")
  public SearchField searchThroughButton(String query) {
    self.shouldBe(visible).setValue(query);
    searchButton.shouldBe(visible).shouldBe(clickable).click();
    return this;
  }
}
