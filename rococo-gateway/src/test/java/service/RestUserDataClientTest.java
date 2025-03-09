package service;

import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.RestUserDataClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestUserDataClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RestUserDataClient restUserDataClient;

    @Captor
    private ArgumentCaptor<HttpEntity<UserJson>> httpEntityCaptor;

    @Captor
    private ArgumentCaptor<URI> uriCaptor;

    private final String baseUri = "http://localhost:8285/internal";
    private final String username = "testuser";
    private final UserJson testUser = new UserJson(
            UUID.randomUUID(),
            username,
            "John",
            "Doe",
            "data:image/png;base64,avatar"
    );

    @BeforeEach
    void setup() {
        restUserDataClient = new RestUserDataClient(restTemplate, "http://localhost:8285");
    }

    @Test
    void getUser_ShouldBuildCorrectUriAndReturnUser() {
        // Mock
        ResponseEntity<UserJson> response = ResponseEntity.ok(testUser);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserJson.class))
        ).thenReturn(response);

        // Test
        UserJson result = restUserDataClient.getUser(username);

        // Verify
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserJson.class)
        );

        URI capturedUri = uriCaptor.getValue();
        assertEquals(baseUri + "/user?username=" + username, capturedUri.toString());
        assertEquals(testUser, result);
    }

    @Test
    void getUser_ShouldThrowExceptionWhenResponseIsEmpty() {
        // Mock
        ResponseEntity<UserJson> response = ResponseEntity.ok(null);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                isNull(),
                eq(UserJson.class)
        )).thenReturn(response);

        // Test & Verify
        assertThrows(NoRestResponseException.class, () ->
                restUserDataClient.getUser(username)
        );
    }

    @Test
    void updateUserInfo_ShouldSendCorrectPatchRequest() {
        // Mock
        ResponseEntity<UserJson> response = ResponseEntity.ok(testUser);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(UserJson.class)
        )).thenReturn(response);

        // Test
        UserJson result = restUserDataClient.updateUserInfo(testUser);

        // Verify
        verify(restTemplate).exchange(
                uriCaptor.capture(),
                eq(HttpMethod.PATCH),
                httpEntityCaptor.capture(),
                eq(UserJson.class)
        );

        assertEquals(baseUri + "/user", uriCaptor.getValue().toString());
        assertEquals(testUser, httpEntityCaptor.getValue().getBody());
        assertEquals(MediaType.APPLICATION_JSON, httpEntityCaptor.getValue().getHeaders().getContentType());
        assertEquals(testUser, result);
    }

    @Test
    void updateUserInfo_ShouldThrowExceptionOnEmptyResponse() {
        // Mock
        ResponseEntity<UserJson> response = ResponseEntity.ok(null);
        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.PATCH),
                any(HttpEntity.class),
                eq(UserJson.class)
        )).thenReturn(response);

        // Test & Verify
        assertThrows(NoRestResponseException.class, () ->
                restUserDataClient.updateUserInfo(testUser)
        );
    }
}