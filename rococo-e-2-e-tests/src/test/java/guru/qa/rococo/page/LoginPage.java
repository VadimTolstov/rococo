package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

public class LoginPage extends BasePage<LoginPage> {
  public static final String URL = CFG.frontUrl() + "/login";

  @NonNull
  @Step("Проверяем, что загрузилась страница авторизации")
  @Override
  public LoginPage checkThatPageLoaded() {
    return this;
  }
}
