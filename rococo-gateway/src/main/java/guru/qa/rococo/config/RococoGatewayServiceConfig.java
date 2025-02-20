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
 * Конфигурационный класс для настройки сервиса Rococo Gateway.
 * Этот класс отвечает за создание и настройку бинов, таких, REST-клиент и документация OpenAPI.
 */
@Configuration
public class RococoGatewayServiceConfig {
    /**
     * Константа, определяющая размер в 1 мб.
     */
    public static final int ONE_MB = 1024 * 1024;

    /**
     * Константа, определяющая схему аутентификации для OpenAPI (Bearer Token).
     */
    public static final String OPEN_API_AUTH_SCHEME = "bearer";

    /**
     * Базовый URI для сервиса пользовательских данных (rococo-userdata).
     */
    private final String rococoUserdataBaseUri;

    /**
     * Базовый URI для сервиса шлюза (rococo-gateway).
     */
    private final String rococoGatewayBaseUri;

    /**
     * Конструктор для внедрения значений из конфигурации.
     *
     * @param rococoUserdataBaseUri Базовый URI для сервиса пользовательских данных.
     * @param rococoGatewayBaseUri  Базовый URI для сервиса шлюза.
     */
    @Autowired
    public RococoGatewayServiceConfig(@Value("${rococo-userdata.base-uri}") String rococoUserdataBaseUri,
                                      @Value("${rococo-gateway.base-uri}") String rococoGatewayBaseUri) {
        this.rococoUserdataBaseUri = rococoUserdataBaseUri;
        this.rococoGatewayBaseUri = rococoGatewayBaseUri;
    }

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

    /**
     * Создает бин {@link OpenAPI} для настройки документации API с использованием Swagger.
     * Этот бин определяет базовый URL сервера, метаинформацию о API и схему аутентификации.
     *
     * @return Настроенный экземпляр {@link OpenAPI}.
     */
    @Bean
    public OpenAPI openAPI() {
        // Создаем объект Server, на котором будет доступно API.
        Server server = new Server();
        // Устанавливаем базовый URL сервера. Переменная rococoGatewayBaseUri должна быть определена где-то в коде.
        server.setUrl(rococoGatewayBaseUri);

        // Создаем и возвращаем объект OpenAPI, который будет содержать всю информацию о документации API.
        return new OpenAPI()
                // Устанавливаем список серверов, на которых доступно API. В данном случае используется только один сервер.
                .servers(List.of(server))
                // Настраиваем метаинформацию о API.
                .info(new Info()
                        .title("Rococo Gateway API Documentation")// Устанавливаем заголовок API.
                        .version("1.0") // Устанавливаем версию API.
                        .description("API documentation with Swagger and SpringDoc"))   // Устанавливаем описание API.
                // Добавляем требование безопасности, указывающее, что для доступа к API требуется аутентификация.
                .addSecurityItem(new SecurityRequirement().addList(OPEN_API_AUTH_SCHEME))
                // Настраиваем компоненты API, такие, как схемы безопасности.
                .components(new Components()
                        // Добавляем схему безопасности с именем OPEN_API_AUTH_SCHEME.
                        .addSecuritySchemes(OPEN_API_AUTH_SCHEME, new SecurityScheme()
                                .name(OPEN_API_AUTH_SCHEME) // Устанавливаем имя схемы безопасности.
                                .type(SecurityScheme.Type.HTTP) // Указываем тип схемы безопасности. В данном случае это HTTP.
                                .scheme("bearer")  // Устанавливаем схему аутентификации. В данном случае используется Bearer Token.
                                .bearerFormat("JWT"))); // Указываем формат токена. В данном случае это JWT (JSON Web Token).
    }
}