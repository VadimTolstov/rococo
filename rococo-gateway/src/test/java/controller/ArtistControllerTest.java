package controller;

import guru.qa.rococo.RococoGatewayApplication;
import guru.qa.rococo.config.SecurityConfigLocal;
import guru.qa.rococo.controller.ArtistController;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.api.RestArtistClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
@ContextConfiguration(classes = {RococoGatewayApplication.class, SecurityConfigLocal.class})
@WithMockUser
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RestArtistClient restArtistClient;

    private final UUID artistId = UUID.randomUUID();
    private final ArtistJson artist = new ArtistJson(
            artistId,
            "Van Gogh",
            "Dutch painter...",
            "data:image/png;base64,iVBORw0KGg..."
    );

    @Test
    void getArtistById() throws Exception {
        when(restArtistClient.getArtistById(artistId)).thenReturn(artist);

        mockMvc.perform(get("/api/artist/{id}", artistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value("Van Gogh"));

        verify(restArtistClient).getArtistById(artistId);
    }

    @Test
    void getAllArtistsWithoutName() throws Exception {
        Page<ArtistJson> page = new PageImpl<>(List.of(artist));
        // 1. Используем любой Pageable в заглушке
        when(restArtistClient.getAllArtists(any(Pageable.class), isNull()))
                .thenReturn(page);

        mockMvc.perform(get("/api/artist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(artistId.toString()));

        // 2. Проверяем вызов с дефолтными параметрами пагинации
        verify(restArtistClient).getAllArtists(
                argThat(pageable ->
                        pageable.getPageNumber() == 0 &&
                                pageable.getPageSize() == 10
                ),
                isNull()
        );
    }

    @Test
    void getAllArtistsWithName() throws Exception {
        Page<ArtistJson> page = new PageImpl<>(List.of(artist));
        when(restArtistClient.getAllArtists(any(Pageable.class), eq("Van")))
                .thenReturn(page);

        mockMvc.perform(get("/api/artist")
                        .param("name", "Van"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Van Gogh"));

        verify(restArtistClient).getAllArtists(PageRequest.of(0, 10), "Van");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addArtist() throws Exception {
        when(restArtistClient.addArtist(any(ArtistJson.class))).thenReturn(artist);

        mockMvc.perform(post("/api/artist")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Van Gogh",
                                    "biography": "Dutch painter...",
                                    "photo": "data:image/png;base64,iVBORw0KGg..."
                                }"""))
                .andExpect(status().isOk());

        verify(restArtistClient).addArtist(argThat(a ->
                a.name().equals("Van Gogh") &&
                        a.biography().equals("Dutch painter...")
        ));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateArtist() throws Exception {
        when(restArtistClient.updateArtist(any(ArtistJson.class))).thenReturn(artist);

        mockMvc.perform(patch("/api/artist")
                        .with(csrf()) // Добавляем CSRF токен
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "%s",
                                    "name": "Van Gogh",
                                    "biography": "Updated bio",
                                    "photo": "data:image/png;base64,iVBORw0KGg..."
                                }""".formatted(artistId))
                )
                .andExpect(status().isOk());

        verify(restArtistClient).updateArtist(argThat(a ->
                a.id().equals(artistId) &&
                        a.biography().equals("Updated bio")
        ));
    }
}
