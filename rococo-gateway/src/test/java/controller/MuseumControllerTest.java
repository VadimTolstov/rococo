package controller;

import guru.qa.rococo.RococoGatewayApplication;
import guru.qa.rococo.config.SecurityConfigLocal;
import guru.qa.rococo.controller.MuseumController;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.api.RestMuseumClient;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MuseumController.class)
@WithMockUser
@ContextConfiguration(classes = {RococoGatewayApplication.class, SecurityConfigLocal.class})
public class MuseumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestMuseumClient restMuseumClient;

    private final UUID museumId = UUID.randomUUID();
    private final UUID countryId = UUID.randomUUID();
    private final MuseumJson museum = new MuseumJson(
            museumId,
            "Лувр",
            "Крупнейший художественный музей мира",
            "data:image/png;base64,iVBORw0KGg...",
            new GeoJson("Париж", new CountryJson(countryId, "Франция"))
    );

    // Тест получения музея по ID
    @Test
    void getMuseumById() throws Exception {
        when(restMuseumClient.getMuseumById(museumId)).thenReturn(museum);

        mockMvc.perform(get("/api/museum/{id}", museumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Лувр"))
                .andExpect(jsonPath("$.geo.country.name").value("Франция"));;

        verify(restMuseumClient).getMuseumById(museumId);
    }

    // Тест получения всех музеев с фильтром по названию
    @Test
    void getAllMuseumsWithTitle() throws Exception {
        Page<MuseumJson> page = new PageImpl<>(List.of(museum));
        when(restMuseumClient.getAllMuseums(any(), eq("Лувр"))).thenReturn(page);

        mockMvc.perform(get("/api/museum")
                        .param("title", "Лувр"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Лувр"));

        verify(restMuseumClient).getAllMuseums(
                argThat(p -> p.getPageNumber() == 0 && p.getPageSize() == 10),
                eq("Лувр")
        );
    }

    // Тест добавления музея с проверкой CSRF
    @Test
    void addMuseum() throws Exception {
        when(restMuseumClient.addMuseum(any())).thenReturn(museum);

        mockMvc.perform(post("/api/museum")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Лувр",
                                    "description": "Крупнейший...",
                                    "photo": "data:image/png;base64,...",
                                    "geo": {
                                        "city": "Париж",
                                        "country": {"id": "%s", "name": "Франция"}
                                    }
                                }""".formatted(countryId)))
                .andExpect(status().isOk());

        verify(restMuseumClient).addMuseum(argThat(m ->
                m.title().equals("Лувр") &&
                        m.geo().country().name().equals("Франция") // Исправленный путь
        ));
    }

    // Тест получения всех стран
    @Test
    void getAllCountries() throws Exception {
        Page<CountryJson> page = new PageImpl<>(List.of(new CountryJson(UUID.randomUUID(), "Россия")));
        when(restMuseumClient.getAllCountries(any())).thenReturn(page);

        mockMvc.perform(get("/api/country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Россия"));
    }

    // Тест обновления музея
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateMuseum() throws Exception {
        when(restMuseumClient.updateMuseum(any())).thenReturn(museum);

        mockMvc.perform(patch("/api/museum")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "id": "%s",
                                    "title": "Обновленный Лувр",
                                    "description": "Новое описание",
                                    "photo": "data:image/png;base64,...",
                                    "geo": {
                                        "city": "Париж",
                                        "country": {"id": "%s", "name": "Франция"}
                                    }
                                }""".formatted(museumId, countryId)))
                .andExpect(status().isOk());

        verify(restMuseumClient).updateMuseum(argThat(m ->
                m.id().equals(museumId) &&
                        m.description().equals("Новое описание")
        ));
    }
}
