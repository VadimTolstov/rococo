package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.ArtistJson;
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
public class RestArtistClient {

    private final RestTemplate restTemplate;
    private final String rococoArtistBaseUri;

    @Autowired
    public RestArtistClient(RestTemplate restTemplate, @Value("${rococo-artist.base-uri}") String rococoArtistBaseUri) {
        this.restTemplate = restTemplate;
        this.rococoArtistBaseUri = rococoArtistBaseUri + "/internal/artist";
    }

    public @Nonnull ArtistJson getArtistById(@Nonnull UUID id) {
        URI uri = UriComponentsBuilder
                .fromUriString(rococoArtistBaseUri)
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        ResponseEntity<ArtistJson> response = restTemplate.exchange(uri, HttpMethod.GET, null, ArtistJson.class);

        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new NotFoundException("Artist not found with id: " + id));
    }
}
