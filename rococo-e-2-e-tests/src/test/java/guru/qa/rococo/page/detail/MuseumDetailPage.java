package guru.qa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.MuseumDetailCondition;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.form.MuseumForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MuseumDetailPage extends BasePage<MuseumDetailPage> {
  public static final String URL = CFG.frontUrl() + "museum/";
  private final SelenideElement pageContainer = $("#appShell");
  private final SelenideElement imageMuseum = pageContainer.$("#page-content");
  private final SelenideElement cardMuseum = imageMuseum.$(".card-header");
  private final SelenideElement buttonEdit = pageContainer.$("[data-testid='edit-museum']");

  @Getter
  protected final Header header = new Header();


  @Override
  @NonNull
  @Step("Проверяем, что загрузилась страница с подробной информацией о Музее.")
  public MuseumDetailPage checkThatPageLoaded() {
    cardMuseum.shouldBe(visible);
    return this;
  }

  @Step("Проверяем полностью карточку Музея.")
  public void checkDetailMuseum(MuseumJson museum) {
    pageContainer.shouldHave(MuseumDetailCondition.hasPainting(museum));
  }

  @NonNull
  @Step("Сравниваем изображение в карточке Музея.")
  public MuseumDetailPage checkImage(BufferedImage images) {
    compareImage(imageMuseum, images);
    return this;
  }

  @NonNull
  @Step("Нажать на кнопку 'Редактировать'")
  public MuseumForm clickEdit() {
    buttonEdit.shouldBe(visible).click();
    return new MuseumForm().checkThatComponentLoaded();
  }

  @Step("Проверяем, что у неавторизованного пользователя кнопка 'Редактировать' не отображается.")
  public void checkNoUpdateMuseumButton() {
    buttonEdit.shouldNot(exist);
  }
}