package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArtistPage extends BasePage<ArtistPage> {
  public static final String URL = CFG.frontUrl() + "/artist";

  @NonNull
  @Step("Проверяем, что загрузилась страница с художниками")
  @Override
  public ArtistPage checkThatPageLoaded() {
    return this;
  }
}
