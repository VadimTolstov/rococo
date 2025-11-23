package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.RococoGatewayApplication;
import guru.qa.rococo.controller.UserDataController;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.RestUserDataClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserDataController.class)
@ContextConfiguration(classes = {RococoGatewayApplication.class})
class UserDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestUserDataClient restUserDataClient;

    @Captor
    private ArgumentCaptor<UserJson> userCaptor;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    private final UUID userId = UUID.randomUUID();
    private final String username = "testuser";
    private final UserJson testUser = new UserJson(
            userId,
            username,
            "John",
            "Doe",
            "data:image/png;base64,avatar"
    );

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwt() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .jwt(builder -> builder.subject(username));
    }

    @Test
    void getCurrentUserShouldExtractUsernameFromJwt() throws Exception {
        // Arrange
        when(restUserDataClient.getUser(username)).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user")
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstname").value("John"));

        // Verify
        verify(restUserDataClient).getUser(username);
    }

    @Test
    void updateUserShouldAddUsernameFromToken() throws Exception {
        UserJson updateRequest = new UserJson(
                null,
                username,
                "Jane",
                "Smith",
                "data:image/png;base64,avatar"
        );

        when(restUserDataClient.updateUserInfo(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstname").value("Jane"));

        verify(restUserDataClient).updateUserInfo(userCaptor.capture());
        UserJson capturedUser = userCaptor.getValue();
        assertEquals(username, capturedUser.username());
        assertEquals("Jane", capturedUser.firstname());
    }

    @Test
    void updateUserShouldIgnoreProvidedUsername() throws Exception {
        UserJson invalidUpdate = new UserJson(
            userId,
            "hacker",
            "Jane",
            "Smith",
            "data:image/png;base64,new-avatar" // Исправлено: должно начинаться с data:image/
        );

        when(restUserDataClient.updateUserInfo(any()))
            .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user")
                .with(jwt())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdate)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(username));

        verify(restUserDataClient).updateUserInfo(userCaptor.capture());
        UserJson capturedUser = userCaptor.getValue();
        assertEquals(username, capturedUser.username());
        assertEquals("Jane", capturedUser.firstname());
    }


    @Test
    void updateUserShouldValidateInput() throws Exception {
        UserJson invalidUser = new UserJson(
                null,
                null,
                "J".repeat(256),
                "D".repeat(256),
                "x".repeat(1024 * 1024 + 1)
        );

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/user")
                        .with(jwt())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Validation failed. Check 'errors' field for details"))
            .andExpect(jsonPath("$.errors").value(
                hasItems(
                    "firstname: Отчество не может быть длиннее 255 символов",
                    "lastname: Фамилия не может быть длиннее 255 символов",
                    "avatar: Размер аватара не должен превышать 1MB"
                )
            ));

        verifyNoInteractions(restUserDataClient);
    }
}