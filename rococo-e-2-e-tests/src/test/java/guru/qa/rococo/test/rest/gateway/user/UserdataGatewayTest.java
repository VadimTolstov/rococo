package guru.qa.rococo.test.rest.gateway.user;


import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.api.gateway.UserdataGatewayApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.HttpException;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("API: Тесты на UserdataGateway")
public class UserdataGatewayTest {
  private static final String IMAGE_DIR = "artists";
  private final UserdataGatewayApiClient userdataGatewayApiClient = new UserdataGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("GET(/api/user) получаем данные пользователя")
  @User
  @ApiLogin
  void geUserTest(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        user.firstname(),
        user.lastname(),
        user.avatar(),
        null
    );
    final UserJson response = userdataGatewayApiClient.getUser(token, 200);
    assertNotNull(response);
    assertEquals(request, response);
  }

  @Test
  @DisplayName("GET(/api/user) ошибка при получение данных пользователя без токена")
  @User
  @ApiLogin
  void geUserNotToken() {
    assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.getUser("", 401));
  }

  @Test
  @DisplayName("GET(/api/user) ошибка при получение данных пользователя с поддельным токеном")
  @User
  @ApiLogin
  void geUserFakeToken() {
    assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.getUser(RandomDataUtils.fakeJwt(), 401));
  }

  @Test
  @DisplayName("PATCH(/api/user) обновление данных пользователя")
  @User
  @ApiLogin
  void updateUser(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    final UserJson response = userdataGatewayApiClient.updateUser(request, token, 200);
    assertEquals(request, response);
  }

  @Test
  @DisplayName("PATCH(/api/user) данные пользователя не обновляются без токена")
  @User
  @ApiLogin
  void updateUserAreNotUpdatedWithoutToken(UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.updateUser(request, "", 401));
  }

  @Test
  @DisplayName("PATCH(/api/user) данные пользователя не обновляются с поддельным токена")
  @User
  @ApiLogin
  void updateUserAreNotUpdatedWithoutFakeToken(UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.updateUser(request, RandomDataUtils.fakeJwt(), 401));
  }


  @Test
  @DisplayName("PATCH(/api/user) firstname пользователя - не может быть длиннее 255 символов")
  @User
  @ApiLogin
  void updateFirstnameUserShouldBeNotLong(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomString.make(256),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.updateUser(request, token, 400));

    userdataGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/user",
        "firstname: Отчество не может быть длиннее 255 символов"
    );
  }

  @Test
  @DisplayName("PATCH(/api/user) firstname пользователя - обновляется с длиной  в 255 символов")
  @User
  @ApiLogin
  void updateFirstnameUserShouldBeMaxLength(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomString.make(255),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    final UserJson response = userdataGatewayApiClient.updateUser(request, token, 200);
    assertEquals(request, response);
  }

  @Test
  @DisplayName("PATCH(/api/user) lastname пользователя - не может быть длиннее 255 символов")
  @User
  @ApiLogin
  void updateLastnameUserShouldBeNotLong(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomString.make(256),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.updateUser(request, token, 400));

    userdataGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/user",
        "lastname: Фамилия не может быть длиннее 255 символов"
    );
  }

  @Test
  @DisplayName("PATCH(/api/user) lastname пользователя - обновляется с длиной  в 255 символов")
  @User
  @ApiLogin
  void updateLastnameUserShouldBeMaxLength(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomString.make(255),
        RandomDataUtils.randomImageString(IMAGE_DIR),
        null
    );

    final UserJson response = userdataGatewayApiClient.updateUser(request, token, 200);
    assertEquals(request, response);
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("POST(/api/artist) при загрузке файла больше 1МБ отображается ошибка")
  void oversizeImageShouldBeValidated(@Token String token, UserJson user) {
    final UserJson request = new UserJson(
        user.id(),
        user.username(),
        RandomDataUtils.randomSurname(),
        RandomDataUtils.randomSurname(),
        PhotoConverter.loadImageAsString(Config.getInstance().imageContentBaseDir() + "/oversize.png"),
        null
    );

    final HttpException ex = assertThrows(HttpException.class,
        () -> userdataGatewayApiClient.updateUser( request, token,400));

    userdataGatewayApiClient.assertError(
        400,
        ex,
        "400",
        "Validation failed. Check 'errors' field for details",
        "/api/user",
        "avatar: Размер аватара не должен превышать 1MB"
    );
  }
}