package guru.qa.niffler.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookiesStore;
import guru.qa.niffler.api.core.interceptor.CodeInterceptor;
import guru.qa.niffler.ex.ApiException;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static guru.qa.niffler.api.core.TokenName.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient extends RestClient {
    private static final String CLIENT_ID = "client";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(),
                true,
                JacksonConverterFactory.create(),
                HttpLoggingInterceptor.Level.BODY,
                new CodeInterceptor()
        );
        authApi = retrofit.create(AuthApi.class);
    }

    /**
     * Метод для запроса формы регистрации для получения CSRF токена.
     */
    @Step("Запрос формы регистрации для получения CSRF токена GET [/register] to niffler-auth")
    public void requestRegisterForm() {
        executeVoid(authApi.requestRegisterForm(), HttpStatus.SC_OK);
    }

    /**
     * Метод для регистрации нового пользователя.
     *
     * @param username       Имя пользователя.
     * @param password       Пароль пользователя.
     * @param passwordSubmit Подтверждение пароля.
     */
    @Step("Регистрация нового пользователя c именем: {username} POST [/register] to niffler-auth")
    public void register(
            @Nonnull String username,
            @Nonnull String password,
            @Nonnull String passwordSubmit) {
        executeVoid(
                authApi.register(
                        username,
                        password,
                        passwordSubmit,
                        ThreadSafeCookiesStore.INSTANCE.cookieValue(CSRF.getCookieName())
                ), HttpStatus.SC_CREATED
        );
    }

    /**
     * Метод для выполнения запроса на авторизацию.
     *
     * @param codeChallenge Код верификатора для OAuth2.
     */
    @Step("Запрос на авторизацию GET [/oauth2/authorize] to niffler-auth")
    public void authorize(@Nonnull String codeChallenge) {
        executeVoid(authApi.authorize(
                        RESPONSE_TYPE,
                        CLIENT_ID,
                        SCOPE,
                        REDIRECT_URI,
                        codeChallenge,
                        CODE_CHALLENGE_METHOD
                ), HttpStatus.SC_OK
        );
    }

    /**
     * Метод для выполнения запроса на вход в систему.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     */
    @Step("Запрос на вход в систему под username = {username}, password = {password} POST [/login] to niffler-auth")
    public void login(String username, String password) {
        executeVoid(authApi.login(
                        username,
                        password,
                        ThreadSafeCookiesStore.INSTANCE.cookieValue(CSRF.getCookieName())
                ), HttpStatus.SC_OK
        );
    }

    /**
     * Метод для получения токена доступа.
     *
     * @param codeVerifier Код верификатора для OAuth2.
     * @return Токен доступа.
     */
    @Step("Получение токена  POST [/oauth2/token] to niffler-auth")
    public String token(String codeVerifier) {
        //    final String code = Objects.requireNonNull(AuthCodeStore.INSTANCE.getCode());
        final JsonNode response = execute(authApi.token(
                ApiLoginExtension.getCode(),
                REDIRECT_URI,
                codeVerifier,
                GRANT_TYPE,
                CLIENT_ID
        ), HttpStatus.SC_OK);

        // AuthCodeStore.INSTANCE.clear();
        return response.get("id_token").asText();
    }


    private void executeVoid(@Nonnull Call<Void> call, int expectedStatusCode) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private @Nonnull <T> T execute(@Nonnull Call<T> call, int expectedStatusCode) {
        try {
            final Response<T> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
            return Objects.requireNonNull(
                    response.body(),
                    "Ответ API вернул null для " + call.request().method() + " " + call.request().url());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }
}
