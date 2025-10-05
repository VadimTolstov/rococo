package guru.qa.rococo.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.component.BaseComponent;
import guru.qa.rococo.page.detail.ArtistDetailPage;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PaintingForm extends BaseComponent<PaintingForm> {

  public PaintingForm() {
    super($(".modal-form"));
  }

  private final SelenideElement inputName = self.$("[name='title']");
  private final SelenideElement inputDescription = self.$("[name='description']");
  private final SelenideElement inputContent = self.$("input[type='file']");
  private final SelenideElement buttonSavePainting = self.$("[type='submit']");
  private final SelenideElement buttonCloseForm = self.$("[type='submit']");
  private final ElementsCollection listArtist = self.$$("select[name='authorId'] option");
  private final ElementsCollection listMuseum = self.$$("select[name='museumId'] option");

  @NonNull
  @Step("Добавляем новую картину.")
  public PaintingPage addPainting(PaintingJson painting) {
    return checkThatComponentLoaded()
        .setName(painting.title())
        .setContent(painting.content())
        .setArtist(painting.artist().name())
        .setDescription(painting.description())
        .setMuseum(painting.museum().title())
        .clickButtonAddPainting(PaintingPage.class);
  }

  @NonNull
  @Step("Добавляем новую картину на странице с подробной информацией о художнике.")
  public ArtistDetailPage addPaintingOnArtistDetailPage(PaintingJson painting) {
    return checkThatComponentLoaded()
        .setName(painting.title())
        .setContent(painting.content())
        .setDescription(painting.description())
        .setMuseum(painting.museum().title())
        .clickButtonAddPainting(ArtistDetailPage.class);
  }

  @NonNull
  @Step("Проверяем, что форма создания/редактирования картины отображается.")
  public PaintingForm checkThatComponentLoaded() {
    getSelf().shouldBe(Condition.visible);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Название картины' {name}.")
  public PaintingForm setName(String name) {
    inputName.setValue(name);
    return this;
  }

  @NonNull
  @Step("Загружаем изображение картины из {path}.")
  public PaintingForm setContent(String path) {
    inputContent.uploadFromClasspath(path);
    return this;
  }

  @NonNull
  @Step("Выбираем автора {artist}.")
  public PaintingForm setArtist(String artist) {
    listArtist.findBy(Condition.exactText(artist))
        .scrollIntoView(true)
        .click();
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Описание картины' {description}.")
  public PaintingForm setDescription(String description) {
    inputDescription.setValue(description);
    return this;
  }

  @NonNull
  @Step("Выбираем музей {museum}.")
  public PaintingForm setMuseum(String museum) {
    listMuseum.findBy(Condition.exactText(museum))
        .scrollIntoView(true)
        .click();
    return this;
  }

  @NonNull
  @Step("Нажимаем кнопку 'Добавить'.")
  public <B> B clickButtonAddPainting(Class<B> clazz) {
    buttonSavePainting.click();
    self.should(Condition.disappear);
    return toPage(clazz);
  }

  @NonNull
  @Step("Нажимаем кнопку 'Закрыть'.")
  public <B> B clickButtonCloseForm(Class<B> clazz) {
    buttonCloseForm.click();
    self.should(Condition.disappear);
    return toPage(clazz);
  }
}
