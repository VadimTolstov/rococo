package guru.qa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.detail.PaintingDetailPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на детальную форму картины")
public class PaintingDetailTest {


  @Content(paintings = @Painting(content = "/paintings/ivan-the-terrible.png"))
  @ScreenShotTest(expected = "painting/painting-detail/ivan-the-terrible.png")
  @DisplayName("В детальной информации о картине отображается изображение картины")
  void paintingShouldBeShown(BufferedImage image, ContentJson content) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .checkImage(image);
  }

  @Content(
      paintingCount = 1
  )
  @Test
  @DisplayName("Подробная информацию о картине отображается")
  public void detailedInformationAboutPaintingIsDisplayed(ContentJson content) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailPainting(painting);
  }

  @Test
  @User
  @ApiLogin
  @Content(
      paintingCount = 1
  )
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму редактирования")
  void authorizedUserShouldCanOpenAddPaintingForm(ContentJson content) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .clickEdit()
        .checkThatComponentLoaded();
  }

  @Content(
      paintingCount = 1
  )
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonAddPainting(ContentJson content) {
    final PaintingJson painting = new ArrayList<>(content.paintings()).getFirst();
    Selenide.open(PaintingDetailPage.URL + painting.id(), PaintingDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdatePaintingButton();
  }
}
