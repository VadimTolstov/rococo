package guru.qa.rococo.test.rest.gateway.painting;


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
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.api.gateway.PaintingGatewayApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.platform.commons.util.StringUtils;
import retrofit2.HttpException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RestTest
@DisplayName("API: Тесты на PaintingGateway")
public class PaintingGatewayTest {
  private static final String IMAGE_DIR = "paintings";
  private final PaintingGatewayApiClient paintingGatewayApiClient = new PaintingGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("POST(/api/painting)  создание картины")
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @User
  @ApiLogin
  void addPaintingSuccessTest(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );

    final PaintingJson response = paintingGatewayApiClient.createPainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }

  @Test
  @DisplayName("POST(/api/painting)  ошибка при создании картины без токина")
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @User
  @ApiLogin
  void addPaintingNotToken(ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, "", 401));
    assertEquals(401, ex.code());
  }


  @Test
  @DisplayName("POST(/api/painting)  ошибка при создании картины с поддельным токином")
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @User
  @ApiLogin
  void addPaintingFakeToken(ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, RandomDataUtils.fakeJwt(), 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("PATCH(/api/painting)  обновление данных картины")
  @User
  @ApiLogin
  @Content(
      paintingCount = 1,
      artistCount = 1,
      museumCount = 1
  )
  void authorizedUserShouldCanEditPainting(@Token String token, ContentJson content) {
    final PaintingJson oldPainting = content.paintings().iterator().next();
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        oldPainting.id(),
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final PaintingJson response = paintingGatewayApiClient.updatePainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertEquals(request.id(), response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }


  @Test
  @DisplayName("PATCH(/api/painting) данные картины не обновляются без токена")
  @User
  @ApiLogin
  @Content(
      paintingCount = 1,
      artistCount = 1,
      museumCount = 1
  )
  void paintingsAreNotUpdatedWithoutToken(ContentJson content) {
    final PaintingJson oldPainting = content.paintings().iterator().next();
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        oldPainting.id(),
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.updatePainting(request, "", 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("PATCH(/api/painting) данные картины не обновляются с поддельным токином")
  @User
  @ApiLogin
  @Content(
      paintingCount = 1,
      artistCount = 1,
      museumCount = 1
  )
  void paintingsAreNotUpdatedWithoutFakeToken(ContentJson content) {
    final PaintingJson oldPainting = content.paintings().iterator().next();
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        oldPainting.id(),
        RandomDataUtils.painting(),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.updatePainting(request, RandomDataUtils.fakeJwt(), 401));
    assertEquals(401, ex.code());
  }

  @Test
  @DisplayName("POST(/api/painting)  ошибка при создании картины без название")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void addPaintingNotNameTest(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        "",
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "title: Название должно содержать от 3 до 255 символов",
        "title: Название обязательно для заполнения, не может быть пустой или состоять только из пробелов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting)  название картины - не может быть короче 3 символов")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void titleShouldBeRequired(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomString.make(2),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "title: Название должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting)  название картины - не может быть длине 255 символов")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void titleShouldBeNotLong(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomString.make(256),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "title: Название должно содержать от 3 до 255 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting)  картина создается с 3 символами в поле 'Название'")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void titleShouldBeMinLength(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomString.make(3),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final PaintingJson response = paintingGatewayApiClient.createPainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }

  @Test
  @DisplayName("POST(/api/painting)  картина создается с 255 символами в поле 'Название'")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void titleShouldBeMaxLength(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomString.make(255),
        RandomDataUtils.shortBio(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final PaintingJson response = paintingGatewayApiClient.createPainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }

  @Test
  @DisplayName("POST(/api/painting)  ошибка при создании картины без описания")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void addPaintingNotDescriptionTest(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        "",
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "description: Описание должно содержать от 10 до 2000 символов",
        "description: Описание обязательно для заполнения, не может быть пустой или состоять только из пробелов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting)  биография картины - не может быть короче 10 символов")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void descriptionShouldBeRequired(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomString.make(9),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "description: Описание должно содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting)  биография картины - не может быть длине 2000 символов")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void descriptionShouldBeNotLong(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomString.make(2001),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );
    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "description: Описание должно содержать от 10 до 2000 символов"
    );
  }

  @Test
  @DisplayName("POST(/api/painting) картина создается с 10 символами в поле 'Описание'")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void descriptionShouldBeMinLength(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomString.make(10),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );

    final PaintingJson response = paintingGatewayApiClient.createPainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }

  @Test
  @DisplayName("POST(/api/painting)  картина создается с 2000 символами в поле 'Описание'")
  @Content(
      artistCount = 1,
      museumCount = 1
  )
  @User
  @ApiLogin
  void descriptionShouldBeMaxLength(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomString.make(2000),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        contentArtist,
        contentMuseum
    );

    final PaintingJson response = paintingGatewayApiClient.createPainting(request, token, 200);
    assertNotNull(response);
    assertAll(
        () -> assertNotNull(response),
        () -> assertNotNull(response.id()),
        () -> assertEquals(request.title(), response.title()),
        () -> assertEquals(request.description(), response.description()),
        () -> assertEquals(request.content(), response.content()),
        () -> assertEquals(request.artist().id(), response.artist().id()),
        () -> assertEquals(request.museum().id(), response.museum().id()));
  }

  @Test
  @Content(paintingCount = 1)
  @DisplayName("GET(/api/painting/{id}) получение картины по  UUID художника")
  void getPaintingByArtistIdTest(ContentJson content) {
    final PaintingJson expected = content.paintings().iterator().next();
    final RestResponsePage<PaintingJson> response = paintingGatewayApiClient.getPaintingsByAuthorId(
        expected.artist().id(),
        null,
        null,
        null,
        200
    );
    final PaintingJson paintingActual = response.getContent().getFirst();
    assertAll(
        () -> assertNotNull(paintingActual),
        () -> assertEquals(expected.id(), paintingActual.id()),
        () -> assertEquals(expected.title(), paintingActual.title()),
        () -> assertEquals(expected.description(), paintingActual.description()),
        () -> assertEquals(expected.content(), paintingActual.content()),
        () -> assertEquals(expected.artist().id(), paintingActual.artist().id()),
        () -> assertEquals(expected.museum().id(), paintingActual.museum().id()));
  }


  @Test
  @Content(paintingCount = 4)
  @DisplayName("GET(/api/painting) получение страницы с картиными")
  void pageTest() {
    RestResponsePage<PaintingJson> response = paintingGatewayApiClient.getPaintings(0, 2, null, null, 200);

    assertNotNull(response);
    assertEquals(2, response.getSize());
    assertTrue(response.getTotalElements() >= 4);
    assertTrue(response.getTotalPages() >= 2);

    List<PaintingJson> responseContent = response.getContent();
    assertEquals(2, responseContent.size());
    assertFalse(StringUtils.isBlank(responseContent.getFirst().title()));
  }

  @Test
  @Content(paintingCount = 4)
  @DisplayName("GET(/api/painting) получение картины по название")
  void getPaintingNameTest(ContentJson content) {
    final PaintingJson expected = content.paintings().iterator().next();
    RestResponsePage<PaintingJson> response = paintingGatewayApiClient.getPaintings(0, 10, null, expected.title(), 200);

    assertNotNull(response);
    assertFalse(response.getContent().isEmpty());

    final PaintingJson responseContent = response.getContent().getFirst();
    assertNotNull(responseContent);
    assertAll(
        () -> assertNotNull(responseContent),
        () -> assertNotNull(responseContent.id()),
        () -> assertEquals(expected.title(), responseContent.title()),
        () -> assertEquals(expected.description(), responseContent.description()),
        () -> assertEquals(expected.content(), responseContent.content()),
        () -> assertEquals(expected.artist().id(), responseContent.artist().id()),
        () -> assertEquals(expected.museum().id(), responseContent.museum().id()));
  }


  @Test
  @Content(
      museumCount = 1,
      artistCount = 1
  )
  @User
  @ApiLogin
  @DisplayName("POST(/api/painting) при загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(@Token String token, ContentJson content) {
    final ArtistJson contentArtist = content.artists().iterator().next();
    final MuseumJson contentMuseum = content.museums().iterator().next();
    final PaintingJson request = new PaintingJson(
        null,
        RandomDataUtils.painting(),
        RandomString.make(2000),
        PhotoConverter.loadImageAsString(Config.getInstance().imageContentBaseDir() + "/oversize.png"),
        contentArtist,
        contentMuseum
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> paintingGatewayApiClient.createPainting(request, token, 400));

    paintingGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/painting",
        "content: Размер фото не должен превышать 1MB"
    );
  }
}
