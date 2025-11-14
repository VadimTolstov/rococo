package guru.qa.rococo.page.detail;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.ArtistDetailCondition;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.component.PaginationComponent;
import guru.qa.rococo.page.form.ArtistForm;
import guru.qa.rococo.page.form.PaintingForm;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistDetailPage extends BasePage<ArtistDetailPage> {
  public static final String URL = CFG.frontUrl() + "artist/";
  private final SelenideElement pageContainer = $("#appShell");
  private final SelenideElement imageArtist = pageContainer.$("#page-content [data-testid='avatar']");
  private final SelenideElement imagePainting = pageContainer.$("#page-content li");
  private final SelenideElement cardArtist = pageContainer.$(".card-header");
  private final SelenideElement buttonEdit = pageContainer.$("[data-testid='edit-artist']");
  private final SelenideElement addPaintingButton = pageContainer.$$("button").findBy(Condition.exactText("Добавить картину"));

  @Getter
  protected final PaginationComponent paginationComponent = new PaginationComponent();

  @Override
  @NonNull
  @Step("Проверяем, что загрузилась страница с подробной информацией о картине.")
  public ArtistDetailPage checkThatPageLoaded() {
    cardArtist.shouldBe(visible, Duration.ofSeconds(10));
    return this;
  }

  @Step("Проверяем полностью карточку Художника .")
  public ArtistDetailPage checkDetailsArtist(ArtistJson artistJson) {
    pageContainer.shouldHave(ArtistDetailCondition.hasPainting(artistJson));
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение Художника.")
  public ArtistDetailPage checkImage(BufferedImage images) {
    compareImage(imageArtist, images);
    return this;
  }

  @NonNull
  @Step("Сравниваем изображение Картин.")
  public ArtistDetailPage checkImagePaintings(BufferedImage images) {
    scrollToElement(imagePainting);
    compareImage(imagePainting, images);
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
  public ArtistForm clickEdit() {
    buttonEdit.shouldBe(visible).click();
    return new ArtistForm().checkThatComponentLoaded();
  }

  @Step("Проверяем, что у неавторизованного пользователя кнопка 'Редактировать' не отображается.")
  public void checkNoUpdateMuseumButton() {
    buttonEdit.shouldNot(exist);
  }

  @Step("Проверяем, что при пустом списке картин у художника отображается текст")
  public void checkMessagePaintingOfEmptyArtistDetailPage() {
    pageContainer.shouldHave(Condition.visible)
        .shouldHave(Condition.text("Пока что список картин этого художника пуст."));
  }
}