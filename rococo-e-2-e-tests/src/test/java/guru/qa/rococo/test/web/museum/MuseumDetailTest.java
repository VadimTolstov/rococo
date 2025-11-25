package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.page.detail.MuseumDetailPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

@WebTest
@DisplayName("Тесты на страницу с подробной информацией о музеи")
public class MuseumDetailTest {

  @Content(museums = @Museum(photo = "/museums/scale.jpeg"))
  @ScreenShotTest(expected = "museum/museum-detail/scale.png")
  @DisplayName("В детальной информации о музее отображается изображение музея")
  void museumShouldBeShown(BufferedImage image, ContentJson content) {
    final MuseumJson museum = new ArrayList<>(content.museums()).getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkImage(image);
  }
/*Тесты на детальную форму картины > Авторизованный пользователь имеет возможность открыть форму редактирования STANDARD_OUT

    09:16:08.112 [ForkJoinPool-1-worker-1] INFO  g.q.rococo.service.api.AuthApiClient - Войдите в систему под: username = [cameron.king Roxana], password = [12345]

    09:16:08.193 [ForkJoinPool-1-worker-1] INFO  c.c.s.WebDriverThreadLocalContainer - No webdriver is bound to current thread: 44 - let's create a new webdriver

    09:16:08.489 [ForkJoinPool-1-worker-3] INFO  c.c.selenide.impl.AttachmentPrinter - [[ATTACHMENT|/rococo/build/reports/tests/1764051368486.347.html]]

    09:16:08.600 [ForkJoinPool-1-worker-3] INFO  c.c.selenide.impl.AttachmentPrinter - [[ATTACHMENT|/rococo/build/reports/tests/1764051368486.347.png]]

    09:16:08.835 [ForkJoinPool-1-worker-2] INFO  c.c.selenide.impl.AttachmentPrinter - [[ATTACHMENT|/rococo/build/reports/tests/1764051368746.349.html]]


Failed to map supported failure 'Element should be Screenshot comparison {.content/img}

Element: '<img alt="Ренуар" class="content__image" src="images/renuar.jpeg" width="90"></img>'

Actual value: Screen comparison failure

Screenshot: file:/rococo/build/reports/tests/1764051368486.347.png

Page source: file:/rococo/build/reports/tests/1764051368486.347.html

Timeout: 8 s.' with mapper 'org.gradle.api.internal.tasks.testing.failure.mappers.OpenTestAssertionFailedMapper@435f2047': Cannot invoke "Object.getClass()" because "expectedValue" is null

Failed to map supported failure 'Element should be Screenshot comparison {#appShell/#page-content li/img}

Element: '<img alt="The Moving Finger Clear skies" class="max-w-full rounded-lg object-cover w-full h-96" src="data:image/png;base64,UklGRsh0AABXRUJQVlA4ILx0AA*/

 @Content(
      museumCount = 1
  )
  @Test
  @DisplayName("Подробная информацию о музее отображается")
  public void detailedInformationAboutMuseumIsDisplayed(ContentJson content) {
    final MuseumJson museum = new ArrayList<>(content.museums()).getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkDetailMuseum(museum);
  }

  @Test
  @User
  @ApiLogin
  @Content(
      museumCount = 1
  )
  @DisplayName("Авторизованный пользователь имеет возможность открыть форму редактирования")
  void authorizedUserShouldCanOpenAddPaintingForm(ContentJson content) {
    final MuseumJson museum = new ArrayList<>(content.museums()).getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .clickEdit()
        .checkThatComponentLoaded();
  }

  @Content(
      museumCount = 1
  )
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки  'Редактировать'")
  void unauthorizedUserDoesNotHaveButtonAddMuseum(ContentJson content) {
    final MuseumJson museum = new ArrayList<>(content.museums()).getFirst();
    Selenide.open(MuseumDetailPage.URL + museum.id(), MuseumDetailPage.class)
        .checkThatPageLoaded()
        .checkNoUpdateMuseumButton();
  }
}
