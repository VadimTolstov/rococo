package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {
  public static final String URL = CFG.frontUrl() + "/register";

  @NonNull
  @Step("Проверяем, что загрузилась страница регистрации")
  @Override
  public RegisterPage checkThatPageLoaded() {
    return this;
  }
}
