package guru.qa.rococo.test.rest.gateway.artist;


import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.api.gateway.ArtistGatewayApiClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
@DisplayName("API: Тесты на ArtistGateway")
public class ArtistGatewayTest {
  private static final String IMAGE_DIR = "artists";
  private final ArtistGatewayApiClient paintingApi = new ArtistGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("Создание художника")
  @User
  @ApiLogin
  void addArtistSuccessTest(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    ArtistJson response = paintingApi.createArtist(request, token, 200);
  }

  @Test
  @DisplayName("Создание художника")
  @User
  @ApiLogin
  void addArtistSuccessTest2(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    ArtistJson response = paintingApi.createArtist(null, token, 200);
  }
}
