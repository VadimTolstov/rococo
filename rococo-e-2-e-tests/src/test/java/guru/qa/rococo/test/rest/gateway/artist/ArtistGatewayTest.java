package guru.qa.rococo.test.rest.gateway.artist;


import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.api.gateway.ArtistGatewayApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.HttpException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("API: Тесты на ArtistGateway")
public class ArtistGatewayTest {
  private static final String IMAGE_DIR = "artists";
  private final ArtistGatewayApiClient artistGatewayApiClient = new ArtistGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("POST(/api/artist)  создание художника")
  @User
  @ApiLogin
  void addArtistSuccessTest(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final ArtistJson response = artistGatewayApiClient.createArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }

  @Test
  @DisplayName("POST(/api/artist)  ошибка при создании художника без токина")
  @User
  @ApiLogin
  void addArtistNotToken() {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, "", 401));
    assertEquals(401, ex.code());
  }


  @Test
  @DisplayName("POST(/api/artist)  ошибка при создании художника с поддельным токеном")
  @User
  @ApiLogin
  void addArtistFakeToken() {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, RandomDataUtils.fakeJwt(), 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("PATCH(/api/artist)  обновление данных художника")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  void authorizedUserShouldCanEditArtist(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final ArtistJson request = new ArtistJson(
        contentArtist.id(),
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final ArtistJson response = artistGatewayApiClient.updateArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.id(), response.id()),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }


  @Test
  @DisplayName("PATCH(/api/artist) данные художника не обновляются без токена")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  void artistsAreNotUpdatedWithoutToken(ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final ArtistJson request = new ArtistJson(
        contentArtist.id(),
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.updateArtist(request, "", 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("PATCH(/api/artist) данные художника не обновляются с поддельным токеном")
  @User
  @ApiLogin
  @Content(artistCount = 1)
  void artistsAreNotUpdatedWithoutFakeToken(ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final ArtistJson request = new ArtistJson(
        contentArtist.id(),
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.updateArtist(request, RandomDataUtils.fakeJwt(), 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("POST(/api/artist)  ошибка при создании художника без имени")
  @User
  @ApiLogin
  void addArtistNotNameTest(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        "",
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "name: Имя обязательно для заполнения, не может быть пустым или состоять только из пробелов",
        "name: Имя должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist)  имя художника - не может быть короче 3 символов")
  @User
  @ApiLogin
  void nameShouldBeRequired(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomString.make(2),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "name: Имя должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist)  имя художника - не может быть длине 255 символов")
  @User
  @ApiLogin
  void nameShouldBeNotLong(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomString.make(256),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "name: Имя должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist)  художник создается с 3 символами в поле 'Имя'")
  @User
  @ApiLogin
  void nameShouldBeMinLength(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomString.make(3),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );

    final ArtistJson response = artistGatewayApiClient.createArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }

  @Test
  @DisplayName("POST(/api/artist)  художник создается с 255 символами в поле 'Имя'")
  @User
  @ApiLogin
  void nameShouldBeMaxLength(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomString.make(255),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );

    final ArtistJson response = artistGatewayApiClient.createArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }

  @Test
  @DisplayName("POST(/api/artist)  ошибка при создании художника без биографии")
  @User
  @ApiLogin
  void addArtistNotBiographyTest(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        "",
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "biography: Биография должна содержать от 10 до 2000 символов",
        "biography: Биография обязательна для заполнения, не может быть пустой или состоять только из пробелов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist)  биография художника - не может быть короче 10 символов")
  @User
  @ApiLogin
  void biographyShouldBeRequired(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomString.make(9),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "biography: Биография должна содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist)  биография художника - не может быть длине 2000 символов")
  @User
  @ApiLogin
  void biographyShouldBeNotLong(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomString.make(2001),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "biography: Биография должна содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/artist) художник создается с 10 символами в поле 'Биография'")
  @User
  @ApiLogin
  void biographyShouldBeMinLength(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomString.make(10),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );

    final ArtistJson response = artistGatewayApiClient.createArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }

  @Test
  @DisplayName("POST(/api/artist)  художник создается с 2000 символами в поле 'Биография'")
  @User
  @ApiLogin
  void biographyShouldBeMaxLength(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomString.make(2000),
        RandomDataUtils.randomImageString(IMAGE_DIR)
    );

    final ArtistJson response = artistGatewayApiClient.createArtist(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.name(), response.name()),
        () -> assertEquals(request.biography(), response.biography()),
        () -> assertEquals(request.photo(), response.photo()));
  }

  @Test
  @DisplayName("GET(/api/artist/{id}) - NOT_FOUND при запросе художника по случайному UUID")
  void artistNotFoundTest() {
    final UUID randomId = UUID.randomUUID();

    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.getArtist(randomId, 404));

    artistGatewayApiClient.assertError(
        404,
        ex,
        "404",
        "Artist not found id:" + randomId,
        "/api/artist/" + randomId,
        String.format("Artist not found id:%s", randomId)
    );
  }

  @Test
  @Content(artistCount = 1)
  @DisplayName("GET(/api/artist/{id}) получение художника по  UUID")
  void getArtistByIdTest(ContentJson content) {
    final ArtistJson expected = content.artists().iterator().next();
    final ArtistJson response = artistGatewayApiClient.getArtist(expected.id(), 200);

    assertNotNull(response);
    assertEquals(expected, response);
  }

  @Test
  @Content(artistCount = 4)
  @DisplayName("GET(/api/artist) получение страницы с художниками")
  void pageTest() {
    RestResponsePage<ArtistJson> response = artistGatewayApiClient.getListArtists(null, 0, 2, null, 200);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<ArtistJson> responseContent = response.getContent();
    assertEquals(2, responseContent.size());
    assertFalse(StringUtils.isBlank(responseContent.getFirst().name()));
  }

  @Test
  @Content(artistCount = 4)
  @DisplayName("GET(/api/artist) получение художника по имени")
  void getArtistNameTest(ContentJson content) {
    final ArtistJson expected = content.artists().iterator().next();
    RestResponsePage<ArtistJson> response = artistGatewayApiClient.getListArtists(expected.name(), 0, 10, null, 200);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    final ArtistJson responseContent = response.getContent().getFirst();
    assertEquals(expected, responseContent);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("POST(/api/artist) при загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(@Token String token) {
    final ArtistJson request = new ArtistJson(
        null,
        RandomDataUtils.randomArtistName(),
        RandomDataUtils.shortBio(),
        PhotoConverter.loadImageAsString(Config.getInstance().imageContentBaseDir() + "/oversize.png")
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> artistGatewayApiClient.createArtist(request, token, 400));

    artistGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/artist",
        "photo: Размер фото не должен превышать 1MB"
    );
  }
}
