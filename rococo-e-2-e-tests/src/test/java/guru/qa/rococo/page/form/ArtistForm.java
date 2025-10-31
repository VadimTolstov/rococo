package guru.qa.rococo.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.component.BaseComponent;
import guru.qa.rococo.page.detail.ArtistDetailPage;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ArtistForm extends BaseComponent<ArtistForm> {

  public ArtistForm() {
    super($(".modal-form"));
  }

  private final SelenideElement inputName = self.$("[name='name']");
  private final SelenideElement inputBiography = self.$("[name='biography']");
  private final SelenideElement inputPhoto = self.$("input[type='file']");
  private final SelenideElement buttonSaveArtist = self.$("[type='submit']");
  private final SelenideElement buttonCloseForm = self.$("[type='button']");

  @NonNull
  @Step("Добавляем нового художника.")
  public ArtistPage addArtist(ArtistJson artist) {
    return checkThatComponentLoaded()
        .setName(artist.name())
        .setPhoto(artist.photo())
        .setBiography(artist.biography())
        .clickButtonAddArtist(ArtistPage.class);
  }

  @NonNull
  @Step("Полностью обновляем данные художника.")
  public ArtistDetailPage fullUpdateArtist(ArtistJson artist) {
   addArtist(artist);
   return  new ArtistDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Проверяем, что форма создания/редактирования художника отображается.")
  public ArtistForm checkThatComponentLoaded() {
    getSelf().shouldBe(Condition.visible);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Имя' {name}.")
  public ArtistForm setName(String name) {
    inputName.setValue(name);
    return this;
  }

  @NonNull
  @Step("Загружаем изображение художника из {path}.")
  public ArtistForm setPhoto(String path) {
    inputPhoto.uploadFromClasspath(path);
    return this;
  }


  @NonNull
  @Step("Заполняем поле 'Биография' {biography}.")
  public ArtistForm setBiography(String biography) {
    inputBiography.setValue(biography);
    return this;
  }

  @NonNull
  @Step("Нажимаем кнопку 'Добавить'.")
  public <B> B clickButtonAddArtist(Class<B> clazz) {
    buttonSaveArtist.click();
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
