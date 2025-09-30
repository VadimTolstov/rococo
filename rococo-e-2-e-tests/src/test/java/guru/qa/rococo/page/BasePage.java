package guru.qa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.config.Config;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement alert = $(".MuiAlert-message");
  protected final ElementsCollection alert2 = $$(".MuiAlert-message");

  public abstract T checkThatPageLoaded();

  @NonNull
  @Step("Проверка отображения сообщения: {message}")
  @SuppressWarnings("unchecked")
  public T checkAlert(String message) {
    alert.shouldHave(Condition.visible).shouldHave(Condition.text(message));
    return (T) this;
  }
  @NonNull
  @Step("Проверка отображения сообщения об ошибке: {expectedText}")
  @SuppressWarnings("unchecked")
  public T checkErrorMessage(String... expectedText) {
    alert2.shouldHave(CollectionCondition.textsInAnyOrder(expectedText));
    return (T) this;
  }

}