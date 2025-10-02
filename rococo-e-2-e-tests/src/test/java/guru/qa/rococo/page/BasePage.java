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
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

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

  @SuppressWarnings("unchecked")
  @NonNull
  @Step("Сравнить изображение")
  public T compareImage(SelenideElement element, BufferedImage expectedImage) {
    Objects.requireNonNull(element, "Element cannot be null");
    Objects.requireNonNull(expectedImage, "Expected image cannot be null");
    element.$("img").shouldBe(image(expectedImage));
    return (T) this;
  }

  @NonNull
  @Step("Сравнить коллекцию изображений")
  @SuppressWarnings("unchecked")
  public T compareImages(List<SelenideElement> element, BufferedImage... expectedImage) {
    Objects.requireNonNull(element, "Element cannot be null");
    Objects.requireNonNull(expectedImage, "Expected image cannot be null");
    if (expectedImage.length == 0 || element.isEmpty() || expectedImage.length != element.size()) {
      throw new IllegalArgumentException(
          String.format("Несоответствие размеров коллекций: элементов на странице=%d, ожидаемых изображений=%d",
              element.size(), expectedImage.length));
    }
    for (int i = 0; i < expectedImage.length; i++) {
      compareImage(element.get(i), expectedImage[i]);
    }
    return (T) this;
  }


  @SuppressWarnings("unchecked")
  @NonNull
  @Step("Проскролить до выбранного элемента")
  public T scrollToElement(SelenideElement element) {
    element.scrollIntoView(true);
    return (T) this;
  }

//  @SuppressWarnings("unchecked")
//  @Step("Проверить, что контент отображается и проскролить до него")
//  public T visible() {
//    self.shouldBe(visible, Duration.ofSeconds(10));
//    Selenide.executeJavaScript(
//        "arguments[0].scrollIntoView({ block: 'center', inline: 'nearest' });",
//        self.toWebElement()
//    );
//    return (T) this;
//  }
}