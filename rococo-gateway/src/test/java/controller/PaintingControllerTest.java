package controller;

import guru.qa.rococo.RococoGatewayApplication;
import guru.qa.rococo.config.SecurityConfigLocal;
import guru.qa.rococo.controller.PaintingController;
import guru.qa.rococo.model.ArtistRef;
import guru.qa.rococo.model.MuseumRef;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.api.RestPaintingClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaintingController.class)
@WithMockUser
@ContextConfiguration(classes = {RococoGatewayApplication.class, SecurityConfigLocal.class})
public class PaintingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestPaintingClient restPaintingClient;

    private final UUID paintingId = UUID.randomUUID();
    private final UUID artistId = UUID.randomUUID();
    private final UUID museumId = UUID.randomUUID();
    private final PaintingJson painting = new PaintingJson(
            paintingId,
            "Самая лучшая картина в мире",
            "Картину рисовали всем музеем в течении 30 лет ........",
            "data:image/png;base64,iVBORw0KGg...",
            new ArtistRef(artistId),
            new MuseumRef(museumId)
    );

    @Test
    void getPaintingById() throws Exception {
        when(restPaintingClient.getPaintingById(paintingId)).thenReturn(painting);

        mockMvc.perform(get("/api/painting/{id}", paintingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Самая лучшая картина в мире"))
                .andExpect(jsonPath("$.description").value("Картину рисовали всем музеем в течении 30 лет ........"))
                .andExpect(jsonPath("$.content").value("data:image/png;base64,iVBORw0KGg..."))
                .andExpect(jsonPath("$.artist.id").value(artistId.toString()))
                .andExpect(jsonPath("$.museum.id").value(museumId.toString()));

        verify(restPaintingClient).getPaintingById(paintingId);
    }

    @Test
    void getAllPaintingWithTitle() throws Exception {
        Page<PaintingJson> page = new PageImpl<>(List.of(painting));
        when(restPaintingClient.getAllPaintings(any(), eq("Самая лучшая картина в мире"))).thenReturn(page);

        mockMvc.perform(get("/api/painting")
                        .param("title", "Самая лучшая картина в мире"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Самая лучшая картина в мире"));

        verify(restPaintingClient).getAllPaintings(
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10),
                eq("Самая лучшая картина в мире")
        );
    }

    @Test
    void addPainting() throws Exception {
        when(restPaintingClient.addPainting(any())).thenReturn(painting);

        mockMvc.perform(post("/api/painting")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Самая лучшая картина в мире",
                                "description": "Картину рисовали всем музеем в течении 30 лет ........",
                                "content": "data:image/png;base64,iVBORw0KGg...",
                                "artist": {"id": "%s"},
                                "museum": {"id": "%s"}
                            }""".formatted(artistId, museumId)))
                .andExpect(status().isOk());

        verify(restPaintingClient).addPainting(argThat(m ->
                m.title().equals("Самая лучшая картина в мире") &&
                        m.description().equals("Картину рисовали всем музеем в течении 30 лет ........")
        ));
    }
//
//    // Тест получения всех стран
//    @Test
//    void getAllCountries() throws Exception {
//        Page<CountryJson> page = new PageImpl<>(List.of(new CountryJson(UUID.randomUUID(), "Россия")));
//        when(restMuseumClient.getAllCountries(any())).thenReturn(page);
//
//        mockMvc.perform(get("/api/country"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].name").value("Россия"));
//    }
//
//    // Тест обновления музея
//    @Test
//    @WithMockUser(roles = "ADMIN")
//    void updateMuseum() throws Exception {
//        when(restMuseumClient.updateMuseum(any())).thenReturn(museum);
//
//        mockMvc.perform(patch("/api/museum")
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "id": "%s",
//                                    "title": "Обновленный Лувр",
//                                    "description": "Новое описание",
//                                    "city": "Париж",
//                                    "photo": "data:image/png;base64,...",
//                                    "country": {"id": "%s", "name": "Франция"}
//                                }""".formatted(museumId, countryId)))
//                .andExpect(status().isOk());
//
//        verify(restMuseumClient).updateMuseum(argThat(m ->
//                m.id().equals(museumId) &&
//                        m.description().equals("Новое описание")
//        ));
//    }

    //  todo Тест обработки 404 ошибки
    /* java
            Copy
    @ControllerAdvice
    public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(HttpClientErrorException.NotFound.class)
        public ResponseEntity<ErrorJson> handleNotFound(HttpClientErrorException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorJson(
                            "not-found",
                            "Not Found",
                            HttpStatus.NOT_FOUND.value(),
                            ex.getMessage()
                    ));
        }

        // Остальные обработчики
    }

    В RestMuseumClient добавьте обработку 404:
    public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
    try {
        // существующий код
    } catch (HttpClientErrorException e) {
        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Museum not found");
        }
        throw e;
    }
}
    @Test
    void getMuseumNotFound() throws Exception {
        when(restMuseumClient.getMuseumById(any())).thenThrow(
                new HttpClientErrorException(HttpStatus.NOT_FOUND)
        );

        mockMvc.perform(get("/api/museum/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
    */
}
