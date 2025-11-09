package guru.qa.rococo.test.rest.gateway.session;


import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.SessionJson;
import guru.qa.rococo.service.api.gateway.SessionGatewayApiClient;
import guru.qa.rococo.utils.JwtDecoder;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.*;

@RestTest
@DisplayName("API: Тесты на SessionGateway")
public class SessionGatewayTest {
  private final SessionGatewayApiClient sessionGatewayApiClient = new SessionGatewayApiClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @DisplayName("GET(/api/session)  получения сесии пользователя без токена")
  void getSessionNotToken() {
    final SessionJson emptySession = sessionGatewayApiClient.getSession("", 200);
    assertAll(
        () -> assertNull(emptySession.username()),
        () -> assertNull(emptySession.issuedAt()),
        () -> assertNull(emptySession.expiresAt())
    );
  }

  @Test
  @DisplayName("GET(/api/session)  получения сесии пользователя с поддельным токеном")
  void getSessionFakeToken() {
    final SessionJson emptySession = sessionGatewayApiClient.getSession(RandomDataUtils.fakeJwt(), 200);
    assertAll(
        () -> assertNull(emptySession.username()),
        () -> assertNull(emptySession.issuedAt()),
        () -> assertNull(emptySession.expiresAt())
    );
  }

  @Test
  @User
  @ApiLogin
  @DisplayName("GET(/api/session)  получения сесии пользователя с поддельным токеном")
  void getSessionToken(@Token String token) {
    final SessionJson session = sessionGatewayApiClient.getSession(token, 200);
    assertAll(() -> {
          assertNotNull(session);
          assertEquals(JwtDecoder.toSessionJson(token), session);
        }
    );
  }
}