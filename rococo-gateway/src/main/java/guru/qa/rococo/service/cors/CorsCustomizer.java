package guru.qa.rococo.service.cors;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Компонент для настройки CORS (Cross-Origin Resource Sharing).
 * Этот класс настраивает политику CORS для HTTP-запросов, разрешая запросы
 * от определенного источника (frontend) и настраивая допустимые методы и заголовки.
 */
@Component
public class CorsCustomizer {

    /**
     * URI фронтенд-приложения, с которого разрешены запросы.
     */
    private final String rococoFrontUri;

    /**
     * Конструктор для внедрения значения URI фронтенд-приложения из конфигурации.
     *
     * @param rococoFrontUri URI фронтенд-приложения, указанный в конфигурации.
     */
    @Autowired
    public CorsCustomizer(@Value("${rococo-front.base-uri}") String rococoFrontUri) {
        this.rococoFrontUri = rococoFrontUri;
    }

    /**
     * Настраивает политику CORS для HTTP-запросов.
     * Этот метод:
     * 1. Разрешает запросы от указанного фронтенд-приложения.
     * 2. Разрешает использование учетных данных (куки, заголовки авторизации).
     * 3. Разрешает все HTTP-методы (GET, POST, PUT, DELETE и т.д.).
     * 4. Разрешает все заголовки.
     *
     * @param http Объект {@link HttpSecurity}, используемый для настройки CORS.
     * @throws Exception В случае ошибки настройки CORS.
     */
    public void corsCustomizer(@Nonnull HttpSecurity http) throws Exception {
        http.cors(c -> {
            // Создаем источник конфигурации CORS.
            CorsConfigurationSource source = s -> {
                // Создаем объект CorsConfiguration для настройки правил CORS.
                CorsConfiguration cc = new CorsConfiguration();

                // Разрешаем использование учетных данных (например, куки, заголовки авторизации).
                // Это важно, если ваш API использует аутентификацию через куки или токены.
                cc.setAllowCredentials(true);

                // Указываем список доменов, с которых разрешены запросы.
                // В данном случае разрешен только домен, указанный в переменной rococoFrontUri.
                cc.setAllowedOrigins(List.of(rococoFrontUri));

                // Разрешаем все заголовки в запросах.
                // Это позволяет клиенту отправлять любые заголовки, включая кастомные.
                cc.setAllowedHeaders(List.of("*"));

                // Разрешаем все HTTP-методы (GET, POST, PUT, DELETE и т.д.).
                // Это позволяет клиенту использовать любые методы для взаимодействия с API.
                cc.setAllowedMethods(List.of("*"));

                // Возвращаем настроенную конфигурацию CORS.
                return cc;
            };

            // Устанавливаем источник конфигурации CORS для HttpSecurity.
            c.configurationSource(source);
        });
    }
}