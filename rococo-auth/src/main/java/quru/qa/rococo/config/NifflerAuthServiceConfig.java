package quru.qa.rococo.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import quru.qa.rococo.config.keys.KeyManager;
import quru.qa.rococo.service.SpecificRequestDumperFilter;
import quru.qa.rococo.service.cors.CorsCustomizer;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.PortMapperImpl;
import org.springframework.security.web.PortResolverImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

/**
 * Конфигурационный класс для настройки OAuth2 Authorization Server.
 * Этот класс определяет конфигурацию для работы с OAuth2 и OpenID Connect,
 * включая настройки безопасности, клиентов, токенов и ключей шифрования.
 * <p>
 * Аннотация @Configuration указывает, что этот класс является конфигурационным и содержит
 * определения бинов, которые будут управляться Spring-контейнером.
 */
@Configuration
public class NifflerAuthServiceConfig {

    // Менеджер ключей для генерации RSA-ключей
    private final KeyManager keyManager;

    // URI фронтенд-приложения
    private final String rococoFrontUri;

    // URI сервера аутентификации
    private final String rococoAuthUri;

    // Идентификатор клиента OAuth2
    private final String clientId;

    // Порт сервера
    private final String serverPort;

    // Порт HTTPS по умолчанию
    private final String defaultHttpsPort = "443";

    // Кастомизатор CORS
    private final CorsCustomizer corsCustomizer;

    // Окружение (профили) приложения
    private final Environment environment;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param keyManager     Менеджер ключей для генерации RSA-ключей.
     * @param rococoFrontUri URI фронтенд-приложения.
     * @param rococoAuthUri  URI сервера аутентификации.
     * @param clientId       Идентификатор клиента OAuth2.
     * @param serverPort     Порт сервера.
     * @param corsCustomizer Кастомизатор CORS.
     * @param environment    Окружение (профили) приложения.
     */
    @Autowired
    public NifflerAuthServiceConfig(KeyManager keyManager,
                                    @Value("${rococo-front.base-uri}") String rococoFrontUri,
                                    @Value("${rococo-auth.base-uri}") String rococoAuthUri,
                                    @Value("${oauth2.client-id}") String clientId,
                                    @Value("${server.port}") String serverPort,
                                    CorsCustomizer corsCustomizer,
                                    Environment environment) {
        this.keyManager = keyManager;
        this.rococoFrontUri = rococoFrontUri;
        this.rococoAuthUri = rococoAuthUri;
        this.clientId = clientId;
        this.serverPort = serverPort;
        this.corsCustomizer = corsCustomizer;
        this.environment = environment;
    }

    /**
     * Настройка SecurityFilterChain для OAuth2 Authorization Server.
     * Этот метод определяет правила безопасности для сервера авторизации,
     * включая настройки OpenID Connect и обработку исключений.
     *
     * @param http       Объект HttpSecurity для настройки безопасности.
     * @param entryPoint Точка входа для аутентификации.
     * @return SecurityFilterChain для OAuth2 Authorization Server.
     * @throws Exception Если произошла ошибка при настройке безопасности.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http,
                                                                      LoginUrlAuthenticationEntryPoint entryPoint) throws Exception {
        // Применяем стандартные настройки безопасности для OAuth2 Authorization Server
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // Добавляем фильтр для логирования запросов в локальном и staging окружении
        if (environment.acceptsProfiles(Profiles.of("local", "staging"))) {
            http.addFilterBefore(new SpecificRequestDumperFilter(
                    new RequestDumperFilter(),
                    "/login", "/oauth2/.*"
            ), DisableEncodeUrlFilter.class);
        }

        // Включаем поддержку OpenID Connect 1.0
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        // Настраиваем обработку исключений и OAuth2 Resource Server
        http.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(entryPoint))
                .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));

        // Применяем кастомизацию CORS
        corsCustomizer.corsCustomizer(http);
        return http.build();
    }

    /**
     * Создает точку входа для аутентификации с принудительным использованием HTTPS.
     * Этот бин используется в окружениях "staging" и "prod".
     *
     * @return LoginUrlAuthenticationEntryPoint с настройками для HTTPS.
     */
    @Bean
    @Profile({"staging", "prod"})
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttps() {
        LoginUrlAuthenticationEntryPoint entryPoint = new LoginUrlAuthenticationEntryPoint("/login");
        PortMapperImpl portMapper = new PortMapperImpl();
        portMapper.setPortMappings(Map.of(
                serverPort, defaultHttpsPort,
                "80", defaultHttpsPort,
                "8080", "8443"
        ));
        PortResolverImpl portResolver = new PortResolverImpl();
        portResolver.setPortMapper(portMapper);
        entryPoint.setForceHttps(true);
        entryPoint.setPortMapper(portMapper);
        entryPoint.setPortResolver(portResolver);
        return entryPoint;
    }

    /**
     * Создает точку входа для аутентификации без принудительного использования HTTPS.
     * Этот бин используется в окружениях "local" и "docker".
     *
     * @return LoginUrlAuthenticationEntryPoint без принудительного HTTPS.
     */
    @Bean
    @Profile({"local", "docker"})
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPointHttp() {
        return new LoginUrlAuthenticationEntryPoint("/login");
    }

    /**
     * Создает репозиторий зарегистрированных клиентов OAuth2.
     * Этот метод регистрирует клиента с идентификатором clientId и настройками для авторизации.
     *
     * @return Репозиторий зарегистрированных клиентов.
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient publicClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri(rococoFrontUri + "/authorized")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)
                        .build()
                )
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.of(2, ChronoUnit.HOURS))
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(publicClient);
    }

    /**
     * Создает кодировщик паролей.
     * Этот метод возвращает DelegatingPasswordEncoder, который поддерживает несколько алгоритмов хеширования.
     *
     * @return Кодировщик паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Создает настройки сервера авторизации.
     * Этот метод возвращает настройки, включая URI сервера аутентификации.
     *
     * @return Настройки сервера авторизации.
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer(rococoAuthUri)
                .build();
    }

    /**
     * Создает источник JWK (JSON Web Key) для подписи и проверки JWT.
     * Этот метод использует RSA-ключи, сгенерированные KeyManager.
     *
     * @return Источник JWK.
     * @throws NoSuchAlgorithmException Если алгоритм генерации ключей не поддерживается.
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        JWKSet set = new JWKSet(keyManager.rsaKey());
        return (jwkSelector, securityContext) -> jwkSelector.select(set);
    }

    /**
     * Создает декодер JWT для проверки токенов.
     * Этот метод использует источник JWK для декодирования токенов.
     *
     * @param jwkSource Источник JWK.
     * @return Декодер JWT.
     */
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
}