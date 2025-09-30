package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {
  public SearchField() {
    super($("input[title='Искать картины...']"));
  }

  private final SelenideElement searchButton = self.parent().$("img[alt='Иконка поиска']");

  @NonNull
  @Step("Ищем поиском  {query}")
  public SearchField search(String query) {
    self.setValue(query);
    return this;
  }
}
