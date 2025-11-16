package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.RococoArtistApplication;
import guru.qa.rococo.model.ArtistJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RococoArtistApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    private final UUID existingArtistId = UUID.fromString("a9c9a9c9-a9c9-a9c9-a9c9-a9c9a9c9a9c9");
    private final UUID nonExistingArtistId = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
    private final String existingArtistName = "Existing Artist";
    private static final String PHOTO_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";

    // Positive tests

    @Test
    @Sql("/testdata/singleArtist.sql")
    void getArtist_shouldReturnArtistById() throws Exception {
        mockMvc.perform(get("/internal/artist/{id}", existingArtistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingArtistId.toString()))
                .andExpect(jsonPath("$.name").value(existingArtistName))
                .andExpect(jsonPath("$.biography").value("Existing Biography"))
                .andExpect(jsonPath("$.photo").exists());
    }

    @Test
    @Sql("/testdata/multipleArtists.sql")
    void getAllArtists_shouldReturnFilteredAndPaginatedResults() throws Exception {
        mockMvc.perform(get("/internal/artist")
                        .param("name", "Art")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @Sql("/testdata/emptyArtists.sql")
    void addArtist_shouldCreateNewArtist() throws Exception {
        ArtistJson newArtist = new ArtistJson(
                null,
                "New Artist",
                "New Biography",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
        );

        mockMvc.perform(post("/internal/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("New Artist"))
                .andExpect(jsonPath("$.photo").isNotEmpty());
    }

    // Negative tests

    @Test
    void getArtist_shouldReturnNotFoundForNonExistingId() throws Exception {
        mockMvc.perform(get("/internal/artist/{id}", nonExistingArtistId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Artist not found id:" + nonExistingArtistId))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.instance").value("/internal/artist/" + nonExistingArtistId));
    }

    @Test
    void updateArtist_shouldReturnNotFoundForNonExistingId() throws Exception {
        ArtistJson updatedArtist = new ArtistJson(
                nonExistingArtistId,
                "Updated Name",
                "Updated Bio",
                null
        );

        mockMvc.perform(patch("/internal/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedArtist)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Artist not found id:" + nonExistingArtistId))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.instance").value("/internal/artist"));
    }

    @Test
    @Sql("/testdata/emptyArtists.sql")
    void addArtist_shouldHandleMaxBiographyLength() throws Exception {
        String maxBio = "A".repeat(2000);
        ArtistJson validArtist = new ArtistJson(
                null,
                "Max Bio Artist",
                maxBio,
                null
        );

        mockMvc.perform(post("/internal/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(validArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.biography").value(maxBio));
    }

    @Test
    @Sql("/testdata/singleArtist.sql")
    void updateArtist_shouldHandlePartialUpdates() throws Exception {
        ArtistJson partialUpdate = new ArtistJson(
                existingArtistId,
                null,  // Name not updated
                "Updated Biography Only",
                null
        );

        mockMvc.perform(patch("/internal/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(existingArtistName))
                .andExpect(jsonPath("$.biography").value("Updated Biography Only"));
    }
    //--------------------------------------------------------

    @Test
    @Sql("/testdata/artistListShouldBeReturned.sql")
    void artistListShouldBeReturned() throws Exception {
        mockMvc.perform(get("/internal/artist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Claude Monet"))
                .andExpect(jsonPath("$.content[0].photo").value(PHOTO_PLACEHOLDER))
                .andExpect(jsonPath("$.content[1].name").value("Pierre-Auguste Renoir"))
                .andExpect(jsonPath("$.content[1].photo").value(PHOTO_PLACEHOLDER));
    }

    @Test
    @Sql("/testdata/artistShouldBeReturnedById.sql")
    void artistShouldBeReturnedById() throws Exception {
        UUID fixtureArtistId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        mockMvc.perform(get("/internal/artist/{id}", fixtureArtistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fixtureArtistId.toString()))
                .andExpect(jsonPath("$.name").value("Vincent van Gogh"))
                .andExpect(jsonPath("$.biography").value("Dutch post-impressionist painter"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER));
    }

    @Test
    void notFoundExceptionShouldBeReturned() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/internal/artist/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Artist not found id:" + nonExistentId));
    }

    @Test
    void artistShouldBeAdded() throws Exception {
        ArtistJson newArtist = new ArtistJson(
                null,
                "Salvador Dali",
                "Spanish surrealist artist",
                PHOTO_PLACEHOLDER
        );

        mockMvc.perform(post("/internal/artist")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(newArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Salvador Dali"))
                .andExpect(jsonPath("$.biography").value("Spanish surrealist artist"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER));
    }

    @Test
    @Sql("/testdata/artistShouldBeUpdated.sql")
    void artistShouldBeUpdated() throws Exception {
        UUID fixtureArtistId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

        ArtistJson updatedArtist = new ArtistJson(
                fixtureArtistId,
                "Leonardo da Vinci",
                "Italian Renaissance polymath",
                PHOTO_PLACEHOLDER
        );

        mockMvc.perform(patch("/internal/artist")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedArtist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(fixtureArtistId.toString()))
                .andExpect(jsonPath("$.name").value("Leonardo da Vinci"))
                .andExpect(jsonPath("$.biography").value("Italian Renaissance polymath"))
                .andExpect(jsonPath("$.photo").value(PHOTO_PLACEHOLDER));
    }

    @Test
    void badRequestExceptionShouldBeThrownOnUpdateWithoutId() throws Exception {
        ArtistJson invalidArtist = new ArtistJson(
                null,
                "Michelangelo",
                "Italian sculptor and painter",
                PHOTO_PLACEHOLDER
        );

        mockMvc.perform(patch("/internal/artist")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidArtist)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("id: ID художника обязателен для заполнения"));
    }
}