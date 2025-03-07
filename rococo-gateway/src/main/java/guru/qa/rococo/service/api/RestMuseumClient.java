package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.MuseumJson;
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
import java.util.Optional;
import java.util.UUID;

@Component
public class RestMuseumClient {
    private final RestTemplate restTemplate;
    private final String rococoMuseumBaseUri;

    @Autowired
    public RestMuseumClient(RestTemplate restTemplate, @Value("${rococo-museum.base-uri}") String rococoMuseumBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoMuseumBaseUri = rococoMuseumBaseUri + "/internal";
    }

    public @Nonnull Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable,
                                                   @Nullable String title) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/museum")
                .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams());

        if (StringUtils.hasText(title)) {
            uriBuilder.queryParam("title", title.trim());
        }

        URI uri = uriBuilder.build().toUri();

        ResponseEntity<RestPage<MuseumJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum GET]"));
    }

    public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/museum/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<MuseumJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                MuseumJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum/{id} GET]"));
    }

    public @Nonnull Page<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/country")
                .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams())
                .build()
                .toUri();

        ResponseEntity<RestPage<CountryJson>> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/country GET]"));
    }

    public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/museum")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MuseumJson> request = new HttpEntity<>(museum, headers);

        ResponseEntity<MuseumJson> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                MuseumJson.class
        );
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum POST]"));
    }

    public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/museum")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MuseumJson> request = new HttpEntity<>(museum, headers);

        ResponseEntity<MuseumJson> response = restTemplate.exchange(
                uri,
                HttpMethod.PATCH,
                request,
                MuseumJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum PATCH]"));
    }
}
