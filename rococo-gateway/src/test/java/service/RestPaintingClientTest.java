package service;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.ArtistRef;
import guru.qa.rococo.model.MuseumRef;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.api.RestPaintingClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestPaintingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestPaintingClient restPaintingClient;

    @Captor
    private ArgumentCaptor<HttpEntity<PaintingJson>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<URI> uriCaptor;

    private final String baseUri = "http://localhost:8284/internal";
    private final UUID paintingId = UUID.randomUUID();
    private final UUID artistId = UUID.randomUUID();
    private final PaintingJson testPainting = new PaintingJson(
            paintingId,
            "Test Painting",
            "Test Description",
            "data:image/png;base64,content",
            new ArtistRef(artistId),
            new MuseumRef(UUID.randomUUID())
    );

    @BeforeEach
    void setup() {
        restPaintingClient = new RestPaintingClient(restTemplate, "http://localhost:8284");
    }

    @Test
    void getAllPaintings_ShouldBuildCorrectUriAndReturnPage() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        RestPage<PaintingJson> restPage = new RestPage<>(List.of(testPainting), pageable, 1);
        ResponseEntity<RestPage<PaintingJson>> response = new ResponseEntity<>(restPage, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        // When
        Page<PaintingJson> result = restPaintingClient.getAllPaintings(pageable, "test");

        // Then
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );

        URI capturedUri = uriCaptor.getValue();
        assertTrue(capturedUri.toString().startsWith(baseUri + "/painting?"));
        assertTrue(capturedUri.toString().contains("page=0"));
        assertTrue(capturedUri.toString().contains("size=10"));
        assertTrue(capturedUri.toString().contains("title=test"));

        assertEquals(1, result.getTotalElements());
        assertEquals(testPainting, result.getContent().get(0));
    }

    @Test
    void getPaintingById_ShouldReturnPainting() {
        // Given
        ResponseEntity<PaintingJson> response = new ResponseEntity<>(testPainting, HttpStatus.OK);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaintingJson.class))
        ).thenReturn(response);

        // When
        PaintingJson result = restPaintingClient.getPaintingById(paintingId);

        // Then
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaintingJson.class)
        );

        assertEquals(baseUri + "/painting/" + paintingId, uriCaptor.getValue().toString());
        assertEquals(testPainting, result);
    }

    @Test
    void getPaintingsByAuthorId_ShouldBuildCorrectUri() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        RestPage<PaintingJson> restPage = new RestPage<>(List.of(testPainting), pageable, 1);
        ResponseEntity<RestPage<PaintingJson>> response = new ResponseEntity<>(restPage, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(response);

        // When
        Page<PaintingJson> result = restPaintingClient.getPaintingsByAuthorId(artistId, pageable);

        // Then
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class)
        );

        URI capturedUri = uriCaptor.getValue();
        assertTrue(capturedUri.toString().startsWith(baseUri + "/painting/author/" + artistId));
        assertTrue(capturedUri.toString().contains("page=0"));
        assertTrue(capturedUri.toString().contains("size=5"));
    }

    @Test
    void addPainting_ShouldSendPostRequestWithBody() {
        // Given
        ResponseEntity<PaintingJson> response = new ResponseEntity<>(testPainting, HttpStatus.CREATED);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(PaintingJson.class))
        ).thenReturn(response);

        // When
        PaintingJson result = restPaintingClient.addPainting(testPainting);

        // Then
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.POST),
                httpEntityCaptor.capture(),
                eq(PaintingJson.class)
        );

        assertEquals(baseUri + "/painting", uriCaptor.getValue().toString());
        assertEquals(testPainting, httpEntityCaptor.getValue().getBody());
        assertEquals(MediaType.APPLICATION_JSON, httpEntityCaptor.getValue().getHeaders().getContentType());
        assertEquals(testPainting, result);
    }

    @Test
    void updatePainting_ShouldSendPatchRequestWithBody() {
        // Given
        ResponseEntity<PaintingJson> response = new ResponseEntity<>(testPainting, HttpStatus.OK);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(PaintingJson.class))
        ).thenReturn(response);

        // When
        PaintingJson result = restPaintingClient.updatePainting(testPainting);

        // Then
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.PATCH),
                httpEntityCaptor.capture(),
                eq(PaintingJson.class)
        );

        assertEquals(baseUri + "/painting", uriCaptor.getValue().toString());
        assertEquals(testPainting, httpEntityCaptor.getValue().getBody());
        assertEquals(testPainting, result);
    }

    @Test
    void getAllPaintings_ShouldThrowExceptionWhenNoResponseBody() {
        // Given
        ResponseEntity<RestPage<PaintingJson>> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                any(ParameterizedTypeReference.class))
        ).thenReturn(response);

        // When & Then
        assertThrows(NoRestResponseException.class, () ->
                restPaintingClient.getAllPaintings(PageRequest.of(0, 10), null)
        );
    }

    @Test
    void getPaintingById_ShouldThrowExceptionWhenEmptyResponse() {
        // Given
        ResponseEntity<PaintingJson> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaintingJson.class))
        ).thenReturn(response);

        // When & Then
        assertThrows(NoRestResponseException.class, () ->
                restPaintingClient.getPaintingById(paintingId)
        );
    }
}