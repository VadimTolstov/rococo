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
import java.util.stream.Collectors;

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
  @DisplayName("PATCH(/api/artist)  обновление данных музея")
  @User
  @ApiLogin
  @Content(museumCount = 1)
  void authorizedUserShouldCanEditMuseum(@Token String token, ContentJson content) {
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        contentMuseum.id(),
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final MuseumJson response = museumGatewayApiClient.updateMuseum(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.photo(), response.photo()),
        () -> assertEquals(request.geo(), response.geo()));
  }


  @Test
  @DisplayName("PATCH(/api/artist) данные музея не обновляются без токена")
  @User
  @ApiLogin
  @Content(museumCount = 1)
  void museumAreNotUpdatedWithoutToken(ContentJson content) {
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        contentMuseum.id(),
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.updateMuseum(request, "", 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("PATCH(/api/artist) данные музея не обновляются с поддельным токином")
  @User
  @ApiLogin
  @Content(museumCount = 1)
  void museumAreNotUpdatedWithoutFakeToken(ContentJson content) {
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final CountryJson country = museumGatewayApiClient
        .getCountries(0, 1, null, 200)
        .iterator()
        .next();
    final MuseumJson request = new MuseumJson(
        contentMuseum.id(),
        RandomDataUtils.museum(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        new GeoJson(RandomDataUtils.city(), country)
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> museumGatewayApiClient.updateMuseum(request, RandomDataUtils.fakeJwt(), 401));
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

    @Test
    @DisplayName("GET(/api/country) получение страницы со странами")
    void pageCountryTest() {
        RestResponsePage<CountryJson> response = museumGatewayApiClient.getCountries(0, 10, null,  200);

        assertNotNull(response);
        assertEquals(10, response.getSize());

        List<CountryJson> responseContent = response.getContent();
        assertEquals(10, responseContent.size());
    }

    @Test
    @DisplayName("GET(/api/country) получение страницы со странами")
    void pageCountryTest2() {
        RestResponsePage<CountryJson> response = museumGatewayApiClient.getCountries(0, 20, null,  200);

        assertNotNull(response);
        assertEquals(20, response.getSize());

        List<CountryJson> responseContent = response.getContent();
        assertEquals(20, responseContent.size());
    }

    @Test
    @DisplayName("GET(/api/country) проверка граничных значений пагинации")
    void pageCountryBoundaryTest() {
        // Тест с минимальным размером страницы
        RestResponsePage<CountryJson> minSizePage = museumGatewayApiClient.getCountries(0, 1, null, 200);
        assertEquals(1, minSizePage.getSize());
        assertEquals(1, minSizePage.getNumberOfElements());
        assertTrue(minSizePage.getTotalPages() >= 193); // как минимум 193 страницы при size=1

        // Тест с большим размером страницы
        RestResponsePage<CountryJson> largeSizePage = museumGatewayApiClient.getCountries(0, 50, null, 200);
        assertEquals(50, largeSizePage.getSize());
        assertEquals(50, largeSizePage.getNumberOfElements());

        // Тест последней страницы
        int totalPages = largeSizePage.getTotalPages();
        RestResponsePage<CountryJson> lastPage = museumGatewayApiClient.getCountries(totalPages - 1, 20, null, 200);
        assertTrue(lastPage.isLast());
        assertFalse(lastPage.isFirst());
    }

    @Test
    @DisplayName("GET(/api/country) проверка обработки некорректной пагинации")
    void pageCountryInvalidPaginationTest() {
        RestResponsePage<CountryJson> negativePage = museumGatewayApiClient.getCountries(-1, 10, null, 200);
        assertEquals(0, negativePage.getNumber(), "Negative page should be treated as page 0");
        assertEquals(10, negativePage.getNumberOfElements());
        assertTrue(negativePage.isFirst());

        RestResponsePage<CountryJson> zeroSize = museumGatewayApiClient.getCountries(0, 0, null, 200);
        assertTrue(zeroSize.getSize() > 0, "Zero size should be replaced with default page size");
        assertTrue(zeroSize.getNumberOfElements() > 0);

        RestResponsePage<CountryJson> negativeSize = museumGatewayApiClient.getCountries(0, -1, null, 200);
        assertTrue(negativeSize.getSize() > 0, "Negative size should be replaced with default page size");
        assertTrue(negativeSize.getNumberOfElements() > 0);

        RestResponsePage<CountryJson> beyondLastPage = museumGatewayApiClient.getCountries(1000, 10, null, 200);
        assertTrue(beyondLastPage.getContent().isEmpty());
        assertEquals(0, beyondLastPage.getNumberOfElements());
        assertTrue(beyondLastPage.isLast());
    }

    @Test
    @DisplayName("GET(/api/country) проверка сортировки по названию")
    void pageCountrySortingTest() {
        RestResponsePage<CountryJson> ascSorted = museumGatewayApiClient.getCountries(0, 20, "name,asc", 200);
        List<CountryJson> ascCountries = ascSorted.getContent();

        for (int i = 0; i < ascCountries.size() - 1; i++) {
            String current = ascCountries.get(i).name();
            String next = ascCountries.get(i + 1).name();
            assertTrue(current.compareToIgnoreCase(next) <= 0,
                    "Countries should be sorted in ascending order: " + current + " should come before " + next);
        }

        RestResponsePage<CountryJson> descSorted = museumGatewayApiClient.getCountries(0, 20, "name,desc", 200);
        List<CountryJson> descCountries = descSorted.getContent();

        for (int i = 0; i < descCountries.size() - 1; i++) {
            String current = descCountries.get(i).name();
            String next = descCountries.get(i + 1).name();
            assertTrue(current.compareToIgnoreCase(next) >= 0,
                    "Countries should be sorted in descending order: " + current + " should come after " + next);
        }
    }

    @Test
    @DisplayName("GET(/api/country) проверка различных параметров сортировки")
    void pageCountryDifferentSortingTest() {
        RestResponsePage<CountryJson> sort1 = museumGatewayApiClient.getCountries(0, 10, "name,asc", 200);
        RestResponsePage<CountryJson> sort2 = museumGatewayApiClient.getCountries(0, 10, "name,desc", 200);

        List<String> ascNames = sort1.getContent().stream()
                .map(CountryJson::name)
                .collect(Collectors.toList());
        List<String> descNames = sort2.getContent().stream()
                .map(CountryJson::name)
                .collect(Collectors.toList());

        assertNotEquals(ascNames, descNames, "Ascending and descending sort should produce different results");
    }
}
