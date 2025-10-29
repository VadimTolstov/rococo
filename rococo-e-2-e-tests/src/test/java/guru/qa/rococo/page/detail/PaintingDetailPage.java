package guru.qa.rococo.page.detail;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.PaintingDetailCondition;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.form.PaintingForm;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PaintingDetailPage extends BasePage<PaintingDetailPage> {
  public static final String URL = CFG.frontUrl() + "painting/";

  private final SelenideElement pageContainer = $("#appShell");
  private final SelenideElement imagePainting = pageContainer.$("#page-content");
  private final SelenideElement cardPainting = imagePainting.$(".card-header");
  private final SelenideElement buttonEdit = pageContainer.$("[data-testid='edit-painting']");

  @Override
  @NonNull
  @Step("Проверяем, что загрузилась страница с подробной информацией о картине.")
  public PaintingDetailPage checkThatPageLoaded() {
    cardPainting.shouldBe(visible);
    return this;
  }

  @NonNull
  @Step("Проверяем полностью карточку картины.")
  public PaintingDetailPage checkDetailPainting(PaintingJson painting) {
    pageContainer.shouldHave(PaintingDetailCondition.hasPainting(painting));
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение в карточке картины.")
  public PaintingDetailPage checkImage(BufferedImage images) {
    compareImage(imagePainting, images);
    return this;
  }


  @NonNull
  @Step("Нажать на кнопку 'Редактировать'")
  public PaintingForm clickEdit() {
    buttonEdit.shouldBe(visible).click();
    return new PaintingForm().checkThatComponentLoaded();
  }

  @Step("Проверяем, что у неавторизованного пользователя кнопка 'Редактировать' не отображается.")
  public void checkNoUpdatePaintingButton() {
    buttonEdit.shouldNot(exist);
  }
}