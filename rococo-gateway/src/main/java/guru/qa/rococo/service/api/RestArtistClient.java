package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.utils.HttpQueryPaginationAndSort;
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
        // Генерация параметров запроса
        HttpQueryPaginationAndSort query = new HttpQueryPaginationAndSort(pageable);
        // 1. Безопасное формирование URL с параметрами пагинации и сортировки
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/artist")
                // Добавляем параметры пагинации
                .queryParams(query.toQueryParams())
                .build()
                .toUri();

        // 2. Выполнение запроса
        ResponseEntity<RestPage<ArtistJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RestPage<ArtistJson>>() {
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

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/artist PATCH]"));
    }
}
