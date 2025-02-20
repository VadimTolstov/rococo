package guru.qa.rococo.config;

import guru.qa.rococo.service.cors.CorsCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Конфигурация безопасности для production окружения.
 * Этот класс настраивает цепочку фильтров безопасности для HTTP-запросов,
 * включая настройку CORS, разрешение доступа к определенным эндпоинтам без аутентификации
 * и настройку OAuth2 Resource Server для аутентификации через JWT.
 */
@EnableWebSecurity
@Configuration
@Profile({"staging", "prod"})
public class SecurityConfigMain {

    /**
     * Компонент для настройки CORS (Cross-Origin Resource Sharing).
     */
    private final CorsCustomizer corsCustomizer;

    /**
     * Конструктор для внедрения зависимости {@link CorsCustomizer}.
     *
     * @param corsCustomizer Компонент для настройки CORS.
     */
    @Autowired
    public SecurityConfigMain(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    /**
     * Настраивает цепочку фильтров безопасности для HTTP-запросов.
     * Этот метод:
     * 1. Настраивает CORS с помощью {@link CorsCustomizer}.
     * 2. Разрешает доступ к определенным эндпоинтам без аутентификации:
     *    - /api/session/current (текущая сессия пользователя)
     *    - /actuator/health (health-check)
     *    - POST-запросы к /graphql (GraphQL API)
     * 3. Требует аутентификации для всех остальных запросов.
     * 4. Настраивает OAuth2 Resource Server для аутентификации через JWT.
     *
     * @param http Объект {@link HttpSecurity} для настройки безопасности.
     * @return Настроенная цепочка фильтров безопасности.
     * @throws Exception В случае ошибки настройки.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Настройка CORS
        corsCustomizer.corsCustomizer(http);

        // Настройка безопасности
        http.authorizeHttpRequests(customizer ->
                customizer.requestMatchers(
                                antMatcher(HttpMethod.GET, "/api/session"),
                                antMatcher("/actuator/health"),
                                antMatcher(HttpMethod.GET, "/api/country/**"),
                                antMatcher(HttpMethod.GET, "/api/artist/**"),
                                antMatcher(HttpMethod.GET, "/api/museum/**"),
                                antMatcher(HttpMethod.GET, "/api/painting/**"))
                        // Разрешить доступ без аутентификации
                        .permitAll()
                        .anyRequest()
                        // Требовать аутентификацию для всех остальных запросов
                        .authenticated()
        ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}