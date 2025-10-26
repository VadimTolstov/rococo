package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {
  private final SelenideElement inputTitle = $(".modal-form");

  @Getter
  protected final SelenideElement self;

  public BaseComponent(SelenideElement self) {
    this.self = self;
  }

  @NonNull
  public <B> B toPage(Class<B> expected) {
    try {
      return expected.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Cannot create instance of " + expected, e);
    }
  }

  @SuppressWarnings("unchecked")//todo он есть не во всех классах
  @Step("Проверяем сообщение {message}")
  public T assertTitleRequired(String message) {
    inputTitle.shouldBe(Condition.text(message));
    return (T) this;
  }
}
