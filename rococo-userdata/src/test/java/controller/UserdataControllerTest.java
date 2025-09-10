package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.RococoUserdataApplication;
import guru.qa.rococo.model.UserJson;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static graphql.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = RococoUserdataApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserdataControllerTest {

    private static final String AVATAR_PLACEHOLDER = "data:image/jpeg;base64,/9j/4AAQSkZ";
    private static final UUID UPDATE_USER_ID = UUID.fromString("777e4567-e89b-12d3-a456-426614174000");
    private static final UUID GET_USER_ID = UUID.fromString("888e4567-e89b-12d3-a456-426614174111");
    private static final String GET_USER_NAME = "Alan";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;


    @Test
    @Sql("/getUserInfo.sql")
    void getUser() throws Exception {
        mockMvc.perform(get("/internal/user").param("username", GET_USER_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(GET_USER_ID.toString()))
                .andExpect(jsonPath("$.username").value(GET_USER_NAME))
                .andExpect(jsonPath("$.firstname").value("Bad"))
                .andExpect(jsonPath("$.lastname").value("Nan"))
                .andExpect(jsonPath("$.avatar").value("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgFFKSL"));
    }

    @Test
    @Sql("/getUserInfo.sql")
    void notFoundUser() throws Exception {
        mockMvc.perform(get("/internal/user").param("username", "Ted"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Пользователь с username = 'Ted' не найден."));
    }

    @Test
    @Sql("/getUserInfo.sql")
    void usermameIsEmpty() throws Exception {
        mockMvc.perform(get("/internal/user").param("username", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Username не должен быть пустой или содержать одни пробелы"));
    }

    @Test
    @Disabled("а может username==null?") //todo
    @Sql("/getUserInfo.sql")
    void badUsername() throws Exception {
        mockMvc.perform(get("/internal/user?username")) // параметр без значения
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Username не должен быть равен null"));
    }


    @Test
    @Sql("/updateUserInfo.sql")
    void fullUpdateUser() throws Exception {
        final UserJson userJson = new UserJson(
                UPDATE_USER_ID,
                "Jone",
                "Smith",
                "Jonson",
                AVATAR_PLACEHOLDER
        );

        mockMvc.perform(patch("/internal/user")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(userJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(UPDATE_USER_ID.toString()))
                .andExpect(jsonPath("$.username").value("Jone"))
                .andExpect(jsonPath("$.firstname").value("Smith"))
                .andExpect(jsonPath("$.lastname").value("Jonson"))
                .andExpect(jsonPath("$.avatar").value(AVATAR_PLACEHOLDER));
    }

    @Test
    @Sql("/updateUserInfo.sql")
    void usernameUpdateUser() throws Exception {
        final String firstName = "John";
        final String lastname = "Doe";
        final String avatar = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
        final UserJson userJson = new UserJson(
                UPDATE_USER_ID,
                "Jone",
                null,
                null,
                null
        );

        mockMvc.perform(patch("/internal/user")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(userJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(UPDATE_USER_ID.toString()))
                .andExpect(jsonPath("$.username").value("Jone"))
                .andExpect(jsonPath("$.firstname").value(firstName))
                .andExpect(jsonPath("$.lastname").value(lastname))
                .andExpect(jsonPath("$.avatar").value(avatar));
    }

    @Test
    @Sql("/updateUserInfo.sql")
    void firstnameUpdateUser() throws Exception {
        final String username = "testUser";
        final String lastname = "Doe";
        final String avatar = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
        final UserJson userJson = new UserJson(
                UPDATE_USER_ID,
                null,
                "Smith",
                null,
                null
        );

        mockMvc.perform(patch("/internal/user")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(userJson)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(UPDATE_USER_ID.toString()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstname").value("Smith"))
                .andExpect(jsonPath("$.lastname").value(lastname))
                .andExpect(jsonPath("$.avatar").value(avatar));
    }

    @Test
    @Sql("/updateUserInfo.sql")
    void replayNameUser400Status() throws Exception {
        final String username = "Atila";
        final UserJson userJson = new UserJson(
                UPDATE_USER_ID,
                username,
                null,
                null,
                null
        );

        mockMvc.perform(patch("/internal/user")
                        .contentType(APPLICATION_JSON)
                        .content(om.writeValueAsString(userJson)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("username: Имя пользователя 'Atila' уже занято."));
    }
}
