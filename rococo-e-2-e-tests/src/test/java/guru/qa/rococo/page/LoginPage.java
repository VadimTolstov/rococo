package guru.qa.rococo.page;

import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;
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
