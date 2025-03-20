package guru.qa.rococo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Конфигурационный класс для настройки сервиса Rococo Painting.
 * Этот класс отвечает за создание и настройку бинов, таких, как REST-клиент.
 */
@Configuration
public class RococoPaintingServiceConfig {
    /**
     * Константа, определяющая размер в 1 мб.
     */
    public static final int ONE_MB = 1024 * 1024;

    /**
     * Создает бин {@link RestTemplate} для выполнения HTTP-запросов к REST-сервисам.
     * Этот бин используется для взаимодействия с REST-сервисами, такими как {@code rococo-spend} или {@code rococo-currency}.
     *
     * @param builder {@link RestTemplateBuilder} для создания и настройки {@link RestTemplate}.
     * @return Настроенный экземпляр {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}