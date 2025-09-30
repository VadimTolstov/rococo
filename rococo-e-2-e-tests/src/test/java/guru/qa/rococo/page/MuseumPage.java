package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MuseumPage extends BasePage<MuseumPage> {

  @NonNull
  @Step("Проверяем, что загрузилась страница с музеями")
  @Override
  public MuseumPage checkThatPageLoaded() {
    return null;
  }
}
