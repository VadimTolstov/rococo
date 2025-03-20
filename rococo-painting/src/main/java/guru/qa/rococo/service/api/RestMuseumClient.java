package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
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
        this.rococoMuseumBaseUri = rococoMuseumBaseUri + "/internal/museum";
    }

    public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoMuseumBaseUri)
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<MuseumJson> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                null,
                MuseumJson.class
        );

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NotFoundException("Museum not found with id: " + id));
    }
}
