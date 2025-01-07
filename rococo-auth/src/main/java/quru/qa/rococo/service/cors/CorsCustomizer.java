package quru.qa.rococo.service.cors;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Класс для кастомизации настроек CORS (Cross-Origin Resource Sharing).
 * Этот класс позволяет настроить политику CORS для HTTP-запросов, что необходимо для
 * обеспечения безопасного взаимодействия между фронтендом и бэкендом, особенно когда
 * они размещены на разных доменах.
 * <p>
 * Аннотация @Component указывает, что этот класс является Spring-компонентом,
 * который будет управляться Spring-контейнером.
 */
@Component
public class CorsCustomizer {

    // URI фронтенд-приложения
    private final String rococoFrontUri;

    // URI сервера аутентификации
    private final String rococoAuthUri;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param rococoFrontUri URI фронтенд-приложения.
     * @param rococoAuthUri  URI сервера аутентификации.
     */
    @Autowired
    public CorsCustomizer(@Value("${rococo-front.base-uri}") String rococoFrontUri,
                          @Value("${rococo-auth.base-uri}") String rococoAuthUri) {
        this.rococoFrontUri = rococoFrontUri;
        this.rococoAuthUri = rococoAuthUri;
    }

    /**
     * Применяет кастомизированные настройки CORS к объекту HttpSecurity.
     *
     * @param http Объект HttpSecurity, к которому применяются настройки CORS.
     * @throws Exception Если произошла ошибка при настройке CORS.
     */
    public void corsCustomizer(@Nonnull HttpSecurity http) throws Exception {
        http.cors(customizer());
    }

    /**
     * Возвращает кастомизатор для настройки CORS.
     *
     * @return Кастомизатор для настройки CORS.
     */
    Customizer<CorsConfigurer<HttpSecurity>> customizer() {
        return c -> c.configurationSource(corsConfigurationSource());
    }

    /**
     * Создает источник конфигурации CORS.
     * Этот метод возвращает CorsConfigurationSource, который определяет, какие домены,
     * заголовки и методы разрешены для CORS-запросов.
     *
     * @return Источник конфигурации CORS.
     */
    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            // Создаем конфигурацию CORS
            CorsConfiguration cc = new CorsConfiguration();
            // Разрешаем использование учетных данных (например, cookies)
            cc.setAllowCredentials(true);
            // Указываем разрешенные домены (frontend и auth server)
            cc.setAllowedOrigins(List.of(rococoFrontUri, rococoAuthUri));
            // Разрешаем все заголовки
            cc.setAllowedHeaders(List.of("*"));
            // Разрешаем все HTTP-методы (GET, POST, PUT, DELETE и т.д.)
            cc.setAllowedMethods(List.of("*"));
            return cc;
        };
    }
}