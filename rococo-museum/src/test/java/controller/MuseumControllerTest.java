package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.RococoMuseumApplication;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RococoMuseumApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MuseumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    // Константы для ID
    private static final UUID LOUVRE_ID = UUID.fromString("555e4567-e89b-12d3-a456-426614174000");
    private static final UUID UFFIZI_ID = UUID.fromString("666e4567-e89b-12d3-a456-426614174001");
    private static final UUID FRANCE_ID = UUID.fromString("111e4567-e89b-12d3-a456-426614174000");
    private static final UUID ITALY_ID = UUID.fromString("222e4567-e89b-12d3-a456-426614174001");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("999e4567-e89b-12d3-a456-426614174999");

    // Константы для тестовых данных
    private static final String VALID_PHOTO = "data:image/jpeg;base64,/9j/4AAQSkZTest";
    private static final String INVALID_PHOTO = "invalid_photo_format";
    private static final String MUSEUM_TITLE = "Test Museum";
    private static final String MUSEUM_DESCRIPTION = "Test description";
    private static final String CITY = "Test City";
    private static final String COUNTRY_NAME = "Test Country";

    // Вспомогательные методы для создания объектов
    private MuseumJson createValidMuseumJson(UUID id, String title, String city, CountryJson country) {
        return new MuseumJson(
                id,
                title,
                MUSEUM_DESCRIPTION,
                VALID_PHOTO,
                new GeoJson(city, country)
        );
    }

    private MuseumJson createBasicMuseumJson(UUID id, String title, String photo, GeoJson geo) {
        return new MuseumJson(
                id,
                title,
                MUSEUM_DESCRIPTION,
                photo,
                geo
        );
    }

    private CountryJson createCountryJson(UUID id, String name) {
        return new CountryJson(id, name);
    }

    private GeoJson createGeoJson(String city, CountryJson country) {
        return new GeoJson(city, country);
    }

    @Test
    @Sql("/countryListShouldBeReturned.sql")
    void getAllCountries_ShouldReturnCountries() throws Exception {
        mockMvc.perform(get("/internal/country")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(FRANCE_ID.toString()))
                .andExpect(jsonPath("$.content[0].name").value("France"))
                .andExpect(jsonPath("$.content[1].id").value(ITALY_ID.toString()))
                .andExpect(jsonPath("$.content[1].name").value("Italy"));
    }

    @Test
    @Sql("/museumListShouldBeReturned.sql")
    void getAllMuseums_ShouldReturnMuseums() throws Exception {
        mockMvc.perform(get("/internal/museum")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(LOUVRE_ID.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Louvre Museum"))
                .andExpect(jsonPath("$.content[0].geo.city").value("Paris"))
                .andExpect(jsonPath("$.content[0].geo.country.name").value("France"))
                .andExpect(jsonPath("$.content[1].id").value(UFFIZI_ID.toString()))
                .andExpect(jsonPath("$.content[1].title").value("Uffizi Gallery"))
                .andExpect(jsonPath("$.content[1].geo.city").value("Florence"))
                .andExpect(jsonPath("$.content[1].geo.country.name").value("Italy"));
    }

    @Test
    @Sql("/museumListShouldBeReturned.sql")
    void getAllMuseums_WithTitleFilter_ShouldReturnFilteredMuseums() throws Exception {
        mockMvc.perform(get("/internal/museum")
                        .param("page", "0")
                        .param("size", "10")
                        .param("title", "Louvre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(LOUVRE_ID.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Louvre Museum"));
    }

    @Test
    @Sql("/museumShouldBeReturnedById.sql")
    void getMuseumById_ShouldReturnMuseum() throws Exception {
        mockMvc.perform(get("/internal/museum/{id}", LOUVRE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(LOUVRE_ID.toString()))
                .andExpect(jsonPath("$.title").value("Louvre Museum"))
                .andExpect(jsonPath("$.description").value("Famous museum in Paris"))
                .andExpect(jsonPath("$.geo.city").value("Paris"))
                .andExpect(jsonPath("$.geo.country.name").value("France"));
    }

    @Test
    void getMuseumById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/internal/museum/{id}", NON_EXISTENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Музей не найден по id: " + NON_EXISTENT_ID));
    }

    @Test
    @Sql("/setupCountryAndGeo.sql")
    void addMuseum_ShouldCreateNewMuseum() throws Exception {
        MuseumJson newMuseum = createValidMuseumJson(
                null,
                "British Museum",
                "London",
                createCountryJson(null, "United Kingdom")
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(newMuseum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("British Museum"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.geo.city").value("London"))
                .andExpect(jsonPath("$.geo.country.name").value("United Kingdom"));
    }

    @Test
    @Sql("/museumShouldBeUpdated.sql")
    void updateMuseum_ShouldUpdateExistingMuseum() throws Exception {
        MuseumJson updatedMuseum = createValidMuseumJson(
                LOUVRE_ID,
                "Louvre Museum Updated",
                "Paris",
                createCountryJson(FRANCE_ID, "France")
        );

        mockMvc.perform(patch("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedMuseum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(LOUVRE_ID.toString()))
                .andExpect(jsonPath("$.title").value("Louvre Museum Updated"))
                .andExpect(jsonPath("$.description").value("Test description"))
                .andExpect(jsonPath("$.geo.city").value("Paris"))
                .andExpect(jsonPath("$.geo.country.name").value("France"));
    }

    @Test
    void addMuseum_WithInvalidPhoto_ShouldReturn400() throws Exception {
        MuseumJson invalidMuseum = createBasicMuseumJson(
                null,
                MUSEUM_TITLE,
                INVALID_PHOTO,
                createGeoJson(CITY, createCountryJson(null, COUNTRY_NAME))
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Фото должно быть в формате data:image"));
    }

    @Test
    void addMuseum_WithoutCity_ShouldReturn400() throws Exception {
        MuseumJson invalidMuseum = createBasicMuseumJson(
                null,
                MUSEUM_TITLE,
                VALID_PHOTO,
                createGeoJson(null, createCountryJson(null, COUNTRY_NAME))
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Город обязателен"));
    }

    @Test
    void addMuseum_WithoutCountry_ShouldReturn400() throws Exception {
        MuseumJson invalidMuseum = createBasicMuseumJson(
                null,
                MUSEUM_TITLE,
                VALID_PHOTO,
                createGeoJson(CITY, null)
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Укажите ID или название страны"));
    }

    @Test
    @Sql("/museumShouldBeUpdated.sql")
    void updateMuseum_WhenNotFound_ShouldReturn404() throws Exception {
        MuseumJson updatedMuseum = createValidMuseumJson(
                NON_EXISTENT_ID,
                "Non Existent Museum",
                CITY,
                createCountryJson(null, COUNTRY_NAME)
        );

        mockMvc.perform(patch("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(updatedMuseum)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Музей не найден по ID: " + NON_EXISTENT_ID));
    }

    @Test
    void addMuseum_WithEmptyTitle_ShouldReturn400() throws Exception {
        MuseumJson invalidMuseum = createBasicMuseumJson(
                null,
                "", // Пустой заголовок
                VALID_PHOTO,
                createGeoJson(CITY, createCountryJson(null, COUNTRY_NAME))
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addMuseum_WithEmptyDescription_ShouldReturn400() throws Exception {
        MuseumJson invalidMuseum = new MuseumJson(
                null,
                MUSEUM_TITLE,
                "", // Пустое описание
                VALID_PHOTO,
                createGeoJson(CITY, createCountryJson(null, COUNTRY_NAME))
        );

        mockMvc.perform(post("/internal/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest());
    }
}