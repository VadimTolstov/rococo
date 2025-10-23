package guru.qa.rococo.test.web.artist;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.ContentJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class ArtistDetailTest {

  @Test
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Авторизованный пользователь может открыть форму изменения художника")
  void authorizedUserShouldCanOpenEditArtistPage(ContentJson content) {
  }


  @Test
  @Content(artistCount = 1)
  @DisplayName("Когда у ходжника нет картин, на детальной странице отображается информация об отсутствии картин")
  void whenArtistHasNotPaintingsThatShownEmptyPaintingsPage(ContentJson content) {
  }


  @Test
  @Content(artistCount = 1, paintingCount = 10)
  @DisplayName("Пагинация списка картин на детальной странице художника работает")
  void artistDetailsPaginationShouldWork(ContentJson content) {
  }
//ArtistDetailPage.url(artist.id()


  @ScreenShotTest(expected = "paintings-list/mona-liza.png")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  @DisplayName("Если пользователь создает карточку картины в форме, открытой через детальную информацию художника, то у художника отображается данная карточка в детальной")
  void whenUserAddPaintingFromArtistDetailThanPaintingShouldShownAtArtistDetail(ContentJson content, BufferedImage expected) {
  }
}
