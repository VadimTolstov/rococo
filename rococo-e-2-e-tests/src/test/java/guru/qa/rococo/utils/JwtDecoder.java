package guru.qa.rococo.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import guru.qa.rococo.model.rest.SessionJson;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class JwtDecoder {

  /**
   * Превращает «сырой» Bearer-токен в SessionJson.
   * Верификацию подписи НЕ делает – только декодирует.
   *
   * @param bearerToken строка вида "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9..."
   * @return SessionJson с заполненными полями username, issuedAt, expiresAt
   * @throws IllegalArgumentException если токен не валиден
   */
  public static @NonNull SessionJson toSessionJson(String bearerToken) {
    if (bearerToken == null ||  bearerToken.isBlank()) {
      throw new IllegalArgumentException("Token must start with 'Bearer '");
    }

    final String token = bearerToken.replaceFirst("(?i)^Bearer\\s+", ""); // отрезаем "Bearer "
    final DecodedJWT jwt = JWT.decode(token);      // парсим без проверки подписи

    return new SessionJson(
        jwt.getSubject(),                       // sub  -> username
        jwt.getIssuedAt(),                      // iat  -> issuedAt
        jwt.getExpiresAt()                      // exp  -> expiresAt
    );
  }
}