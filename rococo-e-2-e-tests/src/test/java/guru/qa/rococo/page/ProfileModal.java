package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ProfileModal extends BasePage<ProfileModal> {

  @NonNull
  @Step("Проверяем, что загрузилась форма профиля")
  @Override
  public ProfileModal checkThatPageLoaded() {
    return this;
  }
}
