package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.RococoPaintingApplication;
import guru.qa.rococo.model.*;
import guru.qa.rococo.service.api.RestArtistClient;
import guru.qa.rococo.service.api.RestMuseumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RococoPaintingApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PaintingControllerTest {

    private static final String PHOTO_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private RestArtistClient restArtistClient;

    @MockBean
    private RestMuseumClient restMuseumClient;

    private UUID paintingId;
    private UUID artistId;
    private UUID museumId;

    @BeforeEach
    void setUp() {
        paintingId = UUID.fromString("777e4567-e89b-12d3-a456-426614174000");
        artistId = UUID.fromString("666e4567-e89b-12d3-a456-426614174001");
        museumId = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");

        when(restArtistClient.getArtistById(artistId)).thenReturn(
                new ArtistJson(artistId, "Jean-Honoré Fragonard", "Famous Rococo artist", null)
        );

        when(restMuseumClient.getMuseumById(museumId)).thenReturn(
                new MuseumJson(museumId, "Louvre Museum", "A museum in Paris", null, new GeoJson("Paris", new CountryJson(UUID.randomUUID(), "France")))
        );
    }

    @Test
    @Sql("/paintingListShouldBeReturned.sql")
    void paintingListShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/painting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("The Swing"))
                .andExpect(jsonPath("$.content[1].title").value("Girl with a Pearl Earring"));
    }

    @Test
    @Sql("/paintingShouldBeReturnedById.sql")
    void paintingShouldBeReturnedById() throws Exception {
        mockMvc.perform(get("/internal/painting/{id}", paintingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paintingId.toString()))
                .andExpect(jsonPath("$.title").value("The Swing"))
                .andExpect(jsonPath("$.description").value("A famous Rococo painting"))
                .andExpect(jsonPath("$.content").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.museum.title").value("Louvre Museum"))
                .andExpect(jsonPath("$.artist.name").value("Jean-Honoré Fragonard"));
    }

    @Test
    void notFoundExceptionShouldBeReturned() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/internal/painting/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Картина не найдена: " + nonExistentId));
    }

    @Test
    @Sql(scripts = {"/setupMuseumAndArtist.sql"})
    void paintingShouldBeAdded() throws Exception {
        PaintingRequestJson newPainting = new PaintingRequestJson(
                null,
                "The Kiss",
                "A famous painting by Gustav Klimt",
                PHOTO_PLACEHOLDER,
                new ArtistRef(artistId),
                new MuseumRef(museumId)
        );

        mockMvc.perform(post("/internal/painting")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(newPainting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Kiss"))
                .andExpect(jsonPath("$.description").value("A famous painting by Gustav Klimt"))
                .andExpect(jsonPath("$.content").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.museum.title").value("Louvre Museum"))
                .andExpect(jsonPath("$.artist.name").value("Jean-Honoré Fragonard"));
    }

    @Test
    @Sql("/paintingShouldBeUpdated.sql")
    void paintingShouldBeUpdated() throws Exception {
        PaintingRequestJson updatedPainting = new PaintingRequestJson(
                paintingId,
                "Updated The Swing",
                "Updated description",
                PHOTO_PLACEHOLDER,
                new ArtistRef(artistId),
                new MuseumRef(museumId)
        );

        mockMvc.perform(patch("/internal/painting")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedPainting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paintingId.toString()))
                .andExpect(jsonPath("$.title").value("Updated The Swing"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.content").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.museum.title").value("Louvre Museum"))
                .andExpect(jsonPath("$.artist.name").value("Jean-Honoré Fragonard"));
    }

    @Test
    @Sql("/paintingByAuthorShouldBeReturned.sql")
    void paintingByAuthorShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/painting/author/{authorId}", artistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].title").value("The Swing"))
                .andExpect(jsonPath("$.content[1].title").value("Girl with a Pearl Earring"));
    }
}