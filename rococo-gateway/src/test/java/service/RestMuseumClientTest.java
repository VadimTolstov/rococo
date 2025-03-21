package service;

import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.api.RestMuseumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestMuseumClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestMuseumClient restMuseumClient;

    private final String baseUri = "http://localhost:8283/internal";
    private final MuseumJson museum = new MuseumJson(
            UUID.randomUUID(),
            "Эрмитаж",
            "Крупнейший музей России",
            "data:image/jpeg;base64,...",
            new GeoJson("Москва", new CountryJson(UUID.randomUUID(), "Россия"))
    );

    @BeforeEach
    void setUp() {
        restMuseumClient = new RestMuseumClient(restTemplate, "http://localhost:8283");
    }

    // Тест получения музея по ID
    @Test
    void getMuseumById() {
        URI expectedUri = URI.create(baseUri + "/museum/" + museum.id());
        when(restTemplate.exchange(eq(expectedUri), eq(HttpMethod.GET), isNull(), eq(MuseumJson.class)))
                .thenReturn(new ResponseEntity<>(museum, HttpStatus.OK));

        MuseumJson result = restMuseumClient.getMuseumById(museum.id());

        assertEquals(museum.title(), result.title());
        verify(restTemplate).exchange(expectedUri, HttpMethod.GET, null, MuseumJson.class);
    }

    // Тест добавления музея с проверкой заголовков
    @Test
    void addMuseum() {
        URI expectedUri = URI.create(baseUri + "/museum");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        when(restTemplate.exchange(
                eq(expectedUri),
                eq(HttpMethod.POST),
                argThat(req -> Objects.equals(req.getHeaders().getContentType(), MediaType.APPLICATION_JSON)),
                eq(MuseumJson.class)
        )).thenReturn(new ResponseEntity<>(museum, HttpStatus.OK));

        MuseumJson result = restMuseumClient.addMuseum(museum);

        assertNotNull(result.id());
        verify(restTemplate).exchange(expectedUri, HttpMethod.POST,
                new HttpEntity<>(museum, headers), MuseumJson.class);
    }

    // Тест получения стран с пагинацией
    @Test
    void getAllCountries() {
        PageRequest pageable = PageRequest.of(1, 20);
        URI expectedUri = UriComponentsBuilder
                .fromUriString(baseUri + "/country")
                .queryParam("page", 1)
                .queryParam("size", 20)
                .build().toUri();

        when(restTemplate.exchange(eq(expectedUri), eq(HttpMethod.GET), isNull(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(new RestPage<>(), HttpStatus.OK));

        restMuseumClient.getAllCountries(pageable);

        verify(restTemplate).exchange(expectedUri, HttpMethod.GET, null,
                new ParameterizedTypeReference<RestPage<CountryJson>>() {
                });
    }

    // Тест обработки ошибки 500
    @Test
    void handleServerError() {
        when(restTemplate.exchange(any(), eq(HttpMethod.GET), isNull(), eq(MuseumJson.class)))
                .thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpServerErrorException.class, () ->
                restMuseumClient.getMuseumById(UUID.randomUUID()));
    }
}
