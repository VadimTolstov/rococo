package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.First;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.PaintingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@First
@DisplayName("Тесты на пустые страницы")
public class EmptyPageTest {

  @Test
  @DisplayName("Проверяем отображения текста, когда список картин пуст")
  void checkMessagePaintingEmpty() {
    Selenide.open(PaintingPage.URL, PaintingPage.class)
        .checkThatPageLoaded()
        .checkMessagePaintingEmpty();
  }

  @Test
  @DisplayName("Проверяем отображения текста, когда список музеев пуст")
  void checkMessageMuseumEmpty() {
    Selenide.open(MuseumPage.URL, MuseumPage.class)
        .checkThatPageLoaded()
        .checkMessageMuseumEmpty();
  }

  @Test
  @DisplayName("Проверяем отображения текста, когда список художников пуст")
  void checkMessageArtisEmpty() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .checkMessageArtisEmpty();
  }
}
