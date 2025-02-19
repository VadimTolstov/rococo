package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestArtistClient {
    private final RestTemplate restTemplate;
    private final String rococoArtistBaseUri;

    @Autowired
    public RestArtistClient(RestTemplate restTemplate, @Value("${rococo-artist.base-uri}") String rococoArtistBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoArtistBaseUri = rococoArtistBaseUri + "/internal";
    }

    public @Nonnull Page<ArtistJson> getAllArtists(@Nonnull Pageable pageable) {
        // 1. Безопасное формирование URL с параметрами пагинации и сортировки
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                // Добавляем параметры пагинации
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        // Добавляем параметры сортировки (если они есть)
        if (pageable.getSort().isSorted()) {
            pageable.getSort().forEach(order ->
                    uriBuilder.queryParam("sort",
                            order.getProperty() + "," + order.getDirection().name().toLowerCase()
                    )
            );
        }

        URI uri = uriBuilder.build().toUri();

        // 2. Выполнение запроса
        ResponseEntity<RestPage<ArtistJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestPage<ArtistJson>>() {
                }
        );

        // 3. Проверка статуса
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NoRestResponseException("Ошибка [/internal/artist/all GET] статус код = " + response.getStatusCode());
        }

        // 4. Возврат результата
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

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NoRestResponseException("Ошибка [/internal/artist/{id} GET] статус код = " + response.getStatusCode());
        }

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist/{id} GET]"));
    }

    public @Nonnull ArtistJson getArtistByName(@Nonnull String name) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                .queryParam("name", name)
                .build()
                .toUri();
        ResponseEntity<ArtistJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                ArtistJson.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NoRestResponseException("Ошибка [/internal/artist?name GET] статус код = " + response.getStatusCode());
        }
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist/{id} GET]"));
    }

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

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NoRestResponseException("Ошибка [/internal/artist POST] статус код = " + response.getStatusCode());
        }
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
                HttpMethod.PATCH,  // Явное указание метода PATCH
                request,
                ArtistJson.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new NoRestResponseException("Ошибка [/internal/artist PATCH] статус код = " + response.getStatusCode());
        }
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist PATCH]"));
    }
}
