package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.utils.HttpQueryPaginationAndSort;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
/**
 * REST-клиент для работы с микросервисом художников.
 * Обеспечивает управление данными о художниках.
 */
@Component
public class RestArtistClient {
    private final RestTemplate restTemplate;
    private final String rococoArtistBaseUri;

    @Autowired
    public RestArtistClient(RestTemplate restTemplate, @Value("${rococo-artist.base-uri}") String rococoArtistBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoArtistBaseUri = rococoArtistBaseUri + "/internal";
    }


    /**
     * Получает страницу художников с фильтрацией по имени.
     *
     * @param pageable Параметры пагинации
     * @param name     Фильтр по имени (может быть null)
     * @return Страница художников {@link Page<ArtistJson>}
     * @throws NoRestResponseException Если ответ сервиса отсутствует
     */
    public @Nonnull Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable,
                                                   @Nullable String name) {
        // 1. Безопасное формирование URL с параметрами пагинации и сортировки
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                // Добавляем параметры пагинации
                .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams());

        if (StringUtils.hasText(name)) {
            uriBuilder.queryParam("name", name.trim());
        }

        URI uri = uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();

        // 2. Выполнение запроса
        ResponseEntity<RestPage<ArtistJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // 3. Возврат результата
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist GET]"));
    }

    public @Nonnull ArtistJson getArtistById(@Nonnull UUID id) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<ArtistJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, ArtistJson.class);

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist/{id} GET]"));
    }

    /**
     * Добавляет нового художника.
     *
     * @param artist Данные художника
     * @return Созданный художник {@link ArtistJson}
     * @throws NoRestResponseException При ошибке связи с сервисом
     */
    public @Nonnull ArtistJson addArtist(@Nonnull ArtistJson artist) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ArtistJson> request = new HttpEntity<>(artist, headers);

        ResponseEntity<ArtistJson> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                ArtistJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist POST]"));
    }

    public @Nonnull ArtistJson updateArtist(@Nonnull ArtistJson artist) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ArtistJson> request = new HttpEntity<>(artist, headers);

        ResponseEntity<ArtistJson> response = restTemplate.exchange(
                uri,
                HttpMethod.PATCH,
                request,
                ArtistJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist PATCH]"));
    }
}
