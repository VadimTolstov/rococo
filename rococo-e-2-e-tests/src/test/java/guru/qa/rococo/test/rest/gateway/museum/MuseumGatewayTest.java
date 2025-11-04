package guru.qa.rococo.test.rest.gateway.museum;


import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.service.api.gateway.MuseumGatewayApiClient;
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
@DisplayName("API: Тесты на MuseumGateway")
public class MuseumGatewayTest {
  private static final String IMAGE_DIR = "museums";
  private final MuseumGatewayApiClient museumGatewayApiClient = new MuseumGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("POST(/api/museum)  создание музея")
  @User
  @ApiLogin
  void addMuseumSuccessTest(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final MuseumJson response = museumGatewayApiClient.createMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }

  @Test
  @DisplayName("POST(/api/museum) ошибка при создании музея без токина")
  @User
  @ApiLogin
  void addMuseumNotToken() {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, "", 401));
    assertEquals(401, ex.code());
  }


  @Test
  @DisplayName("POST(/api/museum)  ошибка при создании музея c невалидным токином")
  @User
  @ApiLogin
  void addMuseumIncorrectToken() {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, RandomDataUtils.fakeJwt(), 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("POST(/api/museum)  ошибка при создании музея без названия")
  @User
  @ApiLogin
  void addMuseumNotNameTest(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        "",
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Название музея должно содержать от 3 до 255 символов",
        "Название музея обязательно для заполнения, не может быть пустым или состоять только из пробелов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum)  имя музея - не может быть короче 3 символов")
  @User
  @ApiLogin
  void nameShouldBeRequired(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomString.make(2),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Название музея должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum)  имя музея - не может быть длине 255 символов")
  @User
  @ApiLogin
  void nameShouldBeNotLong(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomString.make(256),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Название музея должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum)  художник создается с 3 символами в поле 'Имя'")
  @User
  @ApiLogin
  void nameShouldBeMinLength(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomString.make(3),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final MuseumJson response = museumGatewayApiClient.createMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }

  @Test
  @DisplayName("POST(/api/museum)  художник создается с 255 символами в поле 'Имя'")
  @User
  @ApiLogin
  void nameShouldBeMaxLength(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomString.make(255),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final MuseumJson response = museumGatewayApiClient.createMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }

  @Test
  @DisplayName("POST(/api/museum)  ошибка при создании музея без описания")
  @User
  @ApiLogin
  void addMuseumNotDescriptionTest(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        "",
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Описание музея должно должно содержать от 10 до 2000 символов",
        "Описание музея обязательно для заполнения, не может быть пустым или состоять только из пробелов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum)  биография музея - не может быть короче 10 символов")
  @User
  @ApiLogin
  void biographyShouldBeRequired(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomString.make(9),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Описание музея должно должно содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum)  биография музея - не может быть длине 2000 символов")
  @User
  @ApiLogin
  void biographyShouldBeNotLong(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomString.make(2001),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Описание музея должно должно содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/museum) художник создается с 10 символами в поле 'Биография'")
  @User
  @ApiLogin
  void biographyShouldBeMinLength(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomString.make(10),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final MuseumJson response = museumGatewayApiClient.createMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }

  @Test
  @DisplayName("POST(/api/museum)  художник создается с 2000 символами в поле 'Биография'")
  @User
  @ApiLogin
  void biographyShouldBeMaxLength(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomString.make(2000),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final MuseumJson response = museumGatewayApiClient.createMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }

  @Test
  @DisplayName("GET(/api/museum/{id}) - NOT_FOUND при запросе музея по случайному UUID")
  void museumNotFoundTest() {
    final UUID randomId = UUID.randomUUID();

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.getMuseumById(randomId, 404));

    museumGatewayApiClient.assertError(
        404,
        ex,
        "404",
        "Музей не найден по id: " + randomId,
        "/api/museum/" + randomId,
        String.format("Музей не найден по id: %s", randomId)
    );
  }

  @Test
  @Content(museumCount = 1)
  @DisplayName("GET(/api/museum/{id}) получение музея по  UUID")
  void getMuseumByIdTest(ContentJson content) {
    final MuseumJson expected = content.museums().iterator().next();
    final MuseumJson response = museumGatewayApiClient.getMuseumById(expected.id(), 200);

    assertNotNull(response);
    assertEquals(expected, response);
  }

  @Test
  @Content(museumCount = 4)
  @DisplayName("GET(/api/museum) получение страницы с музеями")
  void pageTest() {
    RestResponsePage<MuseumJson> response = museumGatewayApiClient.getMuseums(0, 2, null, null, 200);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<MuseumJson> responseContent = response.getContent();
    assertEquals(2, responseContent.size());
    assertFalse(StringUtils.isBlank(responseContent.getFirst().title()));
  }

  @Test
  @Content(museumCount = 4)
  @DisplayName("GET(/api/museum) получение музея по имени")
  void getMuseumNameTest(ContentJson content) {
    final MuseumJson expected = content.museums().iterator().next();
    RestResponsePage<MuseumJson> response = museumGatewayApiClient.getMuseums(0, 10, null, expected.title(), 200);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    final MuseumJson responseContent = response.getContent().getFirst();
    assertEquals(expected, responseContent);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("POST(/api/museum) при загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(@Token String token) {
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        null,
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        PhotoConverter.loadImageAsString(Config.getInstance().imageContentBaseDir() + "/oversize.png"),
        new GeoJson(RandomDataUtils.city(), country)
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.createMuseum(request, token, 400));

    museumGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/museum",
        "Изображение музея: Размер фото не должен превышать 1MB"
    );
  }
}
