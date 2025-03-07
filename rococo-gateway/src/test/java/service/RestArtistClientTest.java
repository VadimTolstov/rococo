package service;

import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.api.RestArtistClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestArtistClientTest {

    @Mock
    private RestTemplate restTemplate;

    private RestArtistClient restArtistClient;
    private final String baseUri = "http://localhost:8080/internal";
    private final UUID artistId = UUID.randomUUID();
    private final ArtistJson artist = new ArtistJson(
            artistId,
            "Van Gogh",
            "Dutch painter...",
            "data:image/png;base64,iVBORw0KGg..."
    );

    @BeforeEach
    void setUp() {
        restArtistClient = new RestArtistClient(restTemplate, "http://localhost:8080");
    }

    @Test
    void getAllArtistsWithName() {
        PageRequest pageable = PageRequest.of(1, 20);
        String searchName = "Van";

        URI expectedUri = UriComponentsBuilder
                .fromUriString(baseUri + "/artist")
                .queryParam("page", 1)
                .queryParam("size", 20)
                .queryParam("name", searchName)
                .build().toUri();

        ParameterizedTypeReference<RestPage<ArtistJson>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                isNull(),
                eq(typeRef))
        ).thenReturn(new ResponseEntity<>(new RestPage<>(), HttpStatus.OK));

        Page<ArtistJson> result = restArtistClient.getAllArtists(pageable, searchName);

        assertNotNull(result);
        verify(restTemplate).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                isNull(),
                eq(typeRef)
        );
    }

    @Test
    void getAllArtistsWithoutName() {
        PageRequest pageable = PageRequest.of(0, 10);
        URI expectedUri = UriComponentsBuilder
                .fromUriString(baseUri + "/artist")
                .queryParam("page", 0)
                .queryParam("size", 10)
                .build().toUri();

        ParameterizedTypeReference<RestPage<ArtistJson>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                isNull(),
                eq(typeRef))
        ).thenReturn(new ResponseEntity<>(new RestPage<>(), HttpStatus.OK));

        restArtistClient.getAllArtists(pageable, null);

        verify(restTemplate).exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                isNull(),
                eq(typeRef));
    }

    @Test
    void addArtist() {
        URI expectedUri = URI.create(baseUri + "/artist");
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.POST),
                argThat(req ->
                        Objects.equals(req.getBody(), artist) &&
                                Objects.equals(req.getHeaders().getContentType(), MediaType.APPLICATION_JSON)),
                eq(ArtistJson.class))
        ).thenReturn(new ResponseEntity<>(artist, HttpStatus.OK));

        ArtistJson result = restArtistClient.addArtist(artist);

        assertEquals(artist, result);
    }

    @Test
    void updateArtist() {
        URI expectedUri = URI.create(baseUri + "/artist");
        HttpHeaders expectedHeaders = new HttpHeaders();
        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.PATCH),
                argThat(req ->
                        Objects.equals(req.getBody(), artist) &&
                                Objects.equals(req.getHeaders().getContentType(), MediaType.APPLICATION_JSON)),
                eq(ArtistJson.class))
        ).thenReturn(new ResponseEntity<>(artist, HttpStatus.OK));

        ArtistJson result = restArtistClient.updateArtist(artist);

        assertEquals(artist, result);
    }

    @Test
    void getArtistById() {
        URI expectedUri = URI.create(baseUri + "/artist/" + artistId);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.GET),
                isNull(),
                eq(ArtistJson.class))
        ).thenReturn(new ResponseEntity<>(artist, HttpStatus.OK));

        ArtistJson result = restArtistClient.getArtistById(artistId);

        assertEquals(artist, result);
    }
}