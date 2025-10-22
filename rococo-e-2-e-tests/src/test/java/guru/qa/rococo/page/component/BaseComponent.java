package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<?>> {
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
}
