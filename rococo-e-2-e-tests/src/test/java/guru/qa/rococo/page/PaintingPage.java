package guru.qa.rococo.page;

import io.qameta.allure.Step;
import lombok.NonNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PaintingPage extends BasePage<PaintingPage> {
  public static final String URL = CFG.frontUrl() + "/painting";

  @NonNull
  @Step("Проверяем, что загрузилась страница с картинами")
  @Override
  public PaintingPage checkThatPageLoaded() {
    return this;
  }
}
