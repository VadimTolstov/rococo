package guru.qa.rococo.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Stopwatch;
import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.CodeInterceptor;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.AuthClient;
import guru.qa.rococo.utils.OAuthUtils;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

import static guru.qa.rococo.api.core.TokenName.CSRF;

@Slf4j
public class AuthApiClient implements AuthClient, RequestExecutor {
    private static final Config CFG = Config.getInstance();
    private static final String CLIENT_ID = "client";
    private static final String RESPONSE_TYPE = "code";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String SCOPE = "openid";
    private static final String CODE_CHALLENGE_METHOD = "S256";
    private static final String REDIRECT_URI = CFG.frontUrl() + "authorized";

    private final AuthApi authApi;
    private final UserdataApiClient userdataApiClient = new UserdataApiClient();

    public AuthApiClient() {
        authApi = new RestClient.EmtyRestClient(
                CFG.authUrl(),
                true,
                JacksonConverterFactory.create(),
                HttpLoggingInterceptor.Level.BODY,
                new CodeInterceptor()
        ).create(AuthApi.class);
    }

    /**
     * Метод для входа в систему и получения токена.
     *
     * @param username Имя пользователя.
     * @param password Пароль пользователя.
     * @return Токен доступа.
     */
    @Step("Получения token пользователя username = {username}, password = {password}")
    public String singIn(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        ThreadSafeCookieStore.INSTANCE.removeAll();
        log.info("Войдите в систему под: username = [{}], password = [{}]", username, password);

        authorize(OAuthUtils.generateCodeChallenge(codeVerifier));
        login(username, password);
        return token(codeVerifier);
    }

    @Override
    @Step("Создание нового пользователя с именем: {username}")
    public @Nonnull UserJson createUser(@Nonnull String username, @Nonnull String password) {
        requestRegisterForm();
        register(
                username,
                password,
                password
        );

        long maxWaitTime = 5000L;
        Stopwatch sw = Stopwatch.createStarted();
        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            try {
                UserJson userJson = userdataApiClient.getUser(username);
                if (userJson != null || userJson.id() != null) {
                    return userJson; // Пользователь найден, возвращаем
                } else {
                    Thread.sleep(100); // Ожидаем перед следующей проверкой
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Ошибка при выполнении запроса на получение пользователя или ожидании", e);
            }
        }
        // Если пользователь не найден за отведенное время
        throw new AssertionError("Пользователь " + username + " не найден после " + maxWaitTime + "ms");
    }

    /**
     * Метод для запроса формы регистрации для получения CSRF токена.
     */
    @Step("Запрос формы регистрации для получения CSRF токена GET [/register] to rococo-auth")
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
    @Step("Регистрация нового пользователя c именем: {username} POST [/register] to rococo-auth")
    public void register(
            @Nonnull String username,
            @Nonnull String password,
            @Nonnull String passwordSubmit) {
        executeVoid(
                authApi.register(
                        username,
                        password,
                        passwordSubmit,
                        ThreadSafeCookieStore.INSTANCE.cookieValue(CSRF.getCookieName())
                ), HttpStatus.SC_CREATED
        );
    }

    /**
     * Метод для выполнения запроса на авторизацию.
     *
     * @param codeChallenge Код верификатора для OAuth2.
     */
    @Step("Запрос на авторизацию GET [/oauth2/authorize] to rococo-auth")
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
    @Step("Запрос на вход в систему под username = {username}, password = {password} POST [/login] to rococo-auth")
    public void login(String username, String password) {
        executeVoid(authApi.login(
                        username,
                        password,
                        ThreadSafeCookieStore.INSTANCE.cookieValue(CSRF.getCookieName())
                ), HttpStatus.SC_OK
        );
    }

    /**
     * Метод для получения токена доступа.
     *
     * @param codeVerifier Код верификатора для OAuth2.
     * @return Токен доступа.
     */
    @Step("Получение токена  POST [/oauth2/token] to rococo-auth")
    public String token(String codeVerifier) {
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
}
