package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
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
}
