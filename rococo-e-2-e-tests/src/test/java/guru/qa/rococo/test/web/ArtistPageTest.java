package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestContent;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.page.ArtistPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.ParametersAreNonnullByDefault;

@WebTest
@ParametersAreNonnullByDefault
@DisplayName("Тесты на страницу Художники")
public class ArtistPageTest {

  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Добавить Художника'")
  void unauthorizedUserDoesNotHaveButtonAddArtist() {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .checkNoAddPaintingButton();
  }
@Content(artistCount = 1,
    artists = {
        @Artist(name = "Andrei23",
        biography = "Что это такое ты должно работать",
        photo = "artists/shishkin.png")
    }

)
  @Test
  @DisplayName("У неавторизованного пользователя нет кнопки 'Добавить Художника'")
  void unauthorizedUserDoesNotHaveButtonAddArtist2(TestContent  content) {
    Selenide.open(ArtistPage.URL, ArtistPage.class)
        .checkThatPageLoaded()
        .checkNoAddPaintingButton();
  System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" +content.artists().stream().map(ArtistJson::name).toList());
    Selenide.sleep(2000);
  }
}
