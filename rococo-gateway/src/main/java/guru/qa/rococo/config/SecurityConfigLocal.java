package guru.qa.rococo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import guru.qa.rococo.service.cors.CorsCustomizer;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Конфигурация безопасности для окружений local, docker и staging.
 * Этот класс настраивает цепочку фильтров безопасности для HTTP-запросов,
 * включая настройку CORS, отключение CSRF и настройку OAuth2 Resource Server для аутентификации через JWT.
 */
@EnableWebSecurity
@Configuration
@Profile({"local", "docker"})
public class SecurityConfigLocal {

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
    public SecurityConfigLocal(CorsCustomizer corsCustomizer) {
        this.corsCustomizer = corsCustomizer;
    }

    /**
     * Настраивает цепочку фильтров безопасности для HTTP-запросов.
     * Этот метод:
     * 1. Настраивает CORS с помощью {@link CorsCustomizer}.
     * 2. Отключает CSRF (Cross-Site Request Forgery) защиту.
     * 3. Разрешает доступ к определенным эндпоинтам без аутентификации.
     * 4. Требует аутентификации для всех остальных запросов.
     * 5. Настраивает OAuth2 Resource Server для аутентификации через JWT.
     *
     * @param http Объект {@link HttpSecurity} для настройки безопасности.
     * @return Настроенная цепочка фильтров безопасности.
     * @throws Exception В случае ошибки настройки.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Настройка CORS (Cross-Origin Resource Sharing) с использованием кастомного конфигуратора.
        // Это позволяет контролировать, какие домены могут обращаться к вашему API.
        corsCustomizer.corsCustomizer(http);

        // Настройка безопасности для HTTP-запросов.
        http.csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF (Cross-Site Request Forgery) защиту, так как она не нужна для API, использующих токены (например, JWT).
                .authorizeHttpRequests(customizer ->
                        customizer.requestMatchers(
                                        antMatcher(HttpMethod.GET, "/api/session"), // Разрешаем доступ к эндпоинту текущей сессии без аутентификации.
                                        antMatcher(HttpMethod.GET, "/api/country/**"),     // Разрешаем доступ к health-check эндпоинту (для мониторинга).
                                        antMatcher(HttpMethod.GET, "/api/artist/**"),       // Разрешаем доступ к Swagger UI для просмотра документации API.
                                        antMatcher(HttpMethod.GET, "/api/museum/**"),      // Разрешаем доступ к JSON-документации API (Swagger).
                                        antMatcher("/graphiql/**"),         // Разрешаем доступ к GraphiQL (интерфейс для работы с GraphQL).
                                        antMatcher(HttpMethod.GET, "/api/painting/**")) // Разрешаем POST-запросы к GraphQL эндпоинту.
                                .permitAll() // Разрешаем доступ к указанным выше эндпоинтам без аутентификации.
                                .anyRequest()
                                .authenticated() // Для всех остальных запросов требуем аутентификацию.
                )
                // Настройка OAuth2 Resource Server с использованием JWT (JSON Web Token) для аутентификации.
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));

        // Собираем и возвращаем настроенную цепочку фильтров безопасности.
        return http.build();
    }
}