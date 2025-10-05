package guru.qa.rococo.page.detail;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.PaintingDetailCondition;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.form.PaintingForm;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistDetailPage extends BasePage<ArtistDetailPage> {

  private final SelenideElement pageContainer = $("#appShell");
  private final SelenideElement imageArtist = pageContainer.$("#page-content [data-testid='avatar']");
  private final ElementsCollection imagePaintingsList = pageContainer.$$("#page-content li");
  private final SelenideElement cardArtist = pageContainer.$(".card-header");
  private final SelenideElement buttonEdit = pageContainer.$("[data-testid='edit-artist']");
  private final SelenideElement addPaintingButton = pageContainer.$$("button").findBy(Condition.exactText("Добавить картину"));


  @Override
  @NonNull
  @Step("Проверяем, что загрузилась страница с подробной информацией о картине.")
  public ArtistDetailPage checkThatPageLoaded() {
    cardArtist.shouldBe(visible);
    return this;
  }

//  @NonNull todo
//  @Step("Проверяем полностью карточку картины .")
//  public ArtistDetailPage checkDetailPainting(PaintingJson painting) {
//    pageContainer.shouldHave(PaintingDetailCondition.hasPainting(painting));
//    return this;
//  }

  @NonNull
  @Step("Сравниваем изображение Художника.")
  public ArtistDetailPage checkImageArtist(BufferedImage images) {
    compareImage(imageArtist, images);
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение Картин.")
  public ArtistDetailPage checkImagePaintings(BufferedImage... images) {
    compareImages(imagePaintingsList.stream().toList(), images);
    return this;
  }

  @NonNull
  @Step("Нажать на кнопку 'Добавить картину'.")
  public PaintingForm clickAddPaintingButton() {
    addPaintingButton.shouldBe(visible).click();
    return new PaintingForm().checkThatComponentLoaded();
  }

  @NonNull
  @Step("Нажать на кнопку 'Редактировать'")
  public PaintingForm clickEdit() {
    buttonEdit.shouldBe(visible).click();
    return new PaintingForm().checkThatComponentLoaded();
  }
}