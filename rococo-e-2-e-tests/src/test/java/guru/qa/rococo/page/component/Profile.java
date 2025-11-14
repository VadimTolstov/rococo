package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.condition.ScreenshotConditions;
import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class Profile extends BaseComponent<Profile> {
  public Profile() {
    super($(".modal-form"));
  }

  private final SelenideElement buttonExit = self.$$("button[type='button']").findBy(Condition.text("Выйти"));
  private final SelenideElement buttonClosed = self.$$("button[type='button']").findBy(Condition.text("Закрыть"));
  private final SelenideElement buttonUpdateProfile = self.$("button[type='submit']");
  private final SelenideElement inputFile = self.$("input[type='file']");
  private final SelenideElement inputName = self.$("input[name='firstname']");
  private final SelenideElement inputSurname = self.$("input[name='surname']");
  private final SelenideElement imgAvatar = self.$("img");

  @NonNull
  @Step("Полное обновление данных пользователя")
  public <T> T updateProfile(Class<T> tClass, String path, String name, String surname) {
    return setPhoto(path)
        .setName(name)
        .setSurname(surname)
        .clickButtonUpdateProfile(tClass);
  }

  @NonNull
  @Step("Проверяем, что загрузилась форма профиля")
  public Profile checkThatFormLoaded() {
    self.shouldBe(Condition.visible, Duration.ofSeconds(10));
    return this;
  }

  @NonNull
  @Step("Загружаем изображение художника из {path}.")
  public Profile setPhoto(String path) {
    inputFile.uploadFromClasspath(path);
    return this;
  }

  @NonNull
  @Step("Заполняем поле Имя {name}")
  public Profile setName(String name) {
    inputName.setValue(name);
    return this;
  }

  @NonNull
  @Step("Заполняем поле Фамилия {surname}")
  public Profile setSurname(String surname) {
    inputSurname.setValue(surname);
    return this;
  }

  @NonNull
  @Step("Нажать кнопку 'Обновить профиль'")
  public <T> T clickButtonUpdateProfile(Class<T> tClass) {
    buttonUpdateProfile.click();
    return toPage(tClass);
  }

  @NonNull
  @Step("Нажать кнопку 'Выйти'")
  public <T> T clickButtonExitProfile(Class<T> tClass) {
    buttonExit.click();
    return toPage(tClass);
  }

  @NonNull
  @Step("Нажать кнопку 'Закрыть'")
  public <T> T clickButtonClosedProfile(Class<T> tClass) {
    buttonClosed.click();
    return toPage(tClass);
  }

  @NonNull
  @Step("Проверяем Ник пользователя {nick}")
  public Profile checkNick(String nick) {
    self.shouldHave(Condition.text(nick)).shouldHave(Condition.visible);
    return this;
  }

  @NonNull
  @Step("Проверяем Фамилию пользователя {surname}")
  public Profile checkSurname(String surname) {
    inputSurname.shouldHave(Condition.text(surname)).shouldHave(Condition.visible);
    return this;
  }

  @Step("Проверяем изображение аватара")
  public void checkImgAvatar(BufferedImage expected) {
    imgAvatar.shouldBe(ScreenshotConditions.image(expected));
  }

}
