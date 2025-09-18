package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

    /**
     * Метод для выполнения регистрации пользователя.
     *
     * @param username       Имя пользователя.
     * @param password       Пароль.
     * @param passwordSubmit Подтверждение пароля.
     * @param csrf           CSRF-токен.
     * @return Объект Call без тела ответа.
     */
    @POST("/register")
    @FormUrlEncoded
    Call<Void> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf
    );

    @GET("/register")
    Call<Void> requestRegisterForm();

    /**
     * Метод для запроса авторизации.
     *
     * @param responseType       Тип ответа (например, "code").
     * @param clientId           Идентификатор клиента.
     * @param scope              Области доступа.
     * @param redirectUri        URI перенаправления.
     * @param codeChallenge      Запрос на проверку кода.
     * @param codChallengeMethod Метод проверки кода (например, "S256").
     * @return Объект Call с телом ответа типа ResponseBody.
     */
    @GET("/oauth2/authorize")
    Call<Void> authorize(
            @Query("response_type") String responseType,
            @Query("client_id") String clientId,
            @Query("scope") String scope,
            @Query(value = "redirect_uri", encoded = true) String redirectUri,
            @Query("code_challenge") String codeChallenge,
            @Query("code_challenge_method") String codChallengeMethod
    );

    /**
     * Метод для выполнения входа в систему.
     *
     * @param username Имя пользователя.
     * @param password Пароль.
     * @param csrf     CSRF-токен.
     * @return Объект Call без тела ответа.
     */
    @POST("/login")
    @FormUrlEncoded
    Call<Void> login(
            @Field("username") String username,
            @Field("password") String password,
            @Field("_csrf") String csrf
    );

    /**
     * Метод для запроса токена.
     *
     * @param code         Код авторизации.
     * @param redirectUri  URI перенаправления.
     * @param codeVerifier Верификатор кода.
     * @param grantType    Тип предоставления (например, "authorization_code").
     * @param clientId     Идентификатор клиента.
     * @return Объект Call с телом ответа типа JsonNode.
     */
    @POST("/oauth2/token")
    @FormUrlEncoded
    Call<JsonNode> token(
            @Field("code") String code,
            @Field(value = "redirect_uri", encoded = true) String redirectUri,
            @Field("code_verifier") String codeVerifier,
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId
    );
}
