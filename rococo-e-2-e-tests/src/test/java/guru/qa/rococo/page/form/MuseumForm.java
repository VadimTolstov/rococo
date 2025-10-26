package guru.qa.rococo.page.form;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.component.BaseComponent;
import guru.qa.rococo.page.component.PaginationComponent;
import guru.qa.rococo.page.detail.MuseumDetailPage;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MuseumForm extends BaseComponent<MuseumForm> {

  public MuseumForm() {
    super($(".modal-form"));
  }

  private final SelenideElement inputTitle = self.$("[name='title']");
  private final SelenideElement inputDescription = self.$("[name='description']");
  private final SelenideElement inputPhoto = self.$("input[type='file']");
  private final SelenideElement buttonSaveMuseum = self.$("[type='submit']");
  private final SelenideElement buttonCloseForm = self.$("[type='button']");
  private final ElementsCollection listCountry = self.$$("select[name='countryId'] option");
  private final SelenideElement inputCity = self.$("[name='city']");

  final private PaginationComponent pageContainer = new PaginationComponent();

  @NonNull
  @Step("Добавляем новый Музей {museum}")
  public MuseumPage addMuseum(MuseumJson museum) {
    return checkThatComponentLoaded()
        .setTitle(museum.title())
        .setCountry(museum.geo().country().name())
        .setCity(museum.geo().city())
        .setPhoto(museum.photo())
        .setDescription(museum.description())
        .clickButtonAddMuseum(MuseumPage.class);
  }

  @NonNull
  @Step("Редактируем все поля музея {museum}")
  public MuseumDetailPage fullUpdateMuseum(MuseumJson museum) {
    addMuseum(museum);
    return new MuseumDetailPage().checkThatPageLoaded();
  }

  @NonNull
  @Step("Проверяем, что форма создания/редактирования музея отображается.")
  public MuseumForm checkThatComponentLoaded() {
    getSelf().shouldBe(Condition.visible);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Название музея' {name}.")
  public MuseumForm setTitle(String name) {
    inputTitle.setValue(name);
    return this;
  }

  @NonNull
  @Step("Загружаем изображение музея из {path}.")
  public MuseumForm setPhoto(String path) {
    inputPhoto.uploadFromClasspath(path);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Укажите город' {city}.")
  public MuseumForm setCity(String city) {
    inputCity.setValue(city);
    return this;
  }

  @NonNull
  @Step("Заполняем поле 'Описание картины' {description}.")
  public MuseumForm setDescription(String description) {
    inputDescription.setValue(description);
    return this;
  }

  @NonNull
  @Step("Выбираем страну {country}.")
  public MuseumForm setCountry(String country) {
    pageContainer.scrollPagination(listCountry, country).click();
    return this;
  }

  @NonNull
  @Step("Нажимаем кнопку 'Добавить'.")
  public <T> T clickButtonAddMuseum(Class<T> tClass) {
    buttonSaveMuseum.click();
    return toPage(tClass);
  }

  @NonNull
  @Step("Нажимаем кнопку 'Закрыть'.")
  public <T> T clickButtonCloseForm(Class<T> tClass) {
    buttonCloseForm.click();
    self.should(Condition.disappear);
    return toPage(tClass);
  }
}
