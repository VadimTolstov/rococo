package service;

import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.SameUsernameException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.UserdataService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserdataServiceTest {
    @Mock
    private UserdataRepository userdataRepository;

    private UserdataService userdataService;

    private UUID userId;
    private UserEntity userEntity;

    private static final String AVATAR = "data:image/jpeg;base64,/9j/4AAQSkZ";
    private static final String AVATAR2 = "data:image/jpeg;base64,/8j/4AAQSkLasSAFG";
    private static final String USER_NAME = "Ali";
    private static final String FIRST_NAME = "Jac";
    private static final String LAST_NAME = "Jochen";

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername(USER_NAME);
        userEntity.setFirstname(FIRST_NAME);
        userEntity.setLastname(LAST_NAME);
        userEntity.setAvatar(AVATAR.getBytes(StandardCharsets.UTF_8));
        userdataService = new UserdataService(userdataRepository);
    }

    @Test
    void getUserByUsername() {
        Mockito.when(userdataRepository.findByUsername(USER_NAME)).thenReturn(Optional.of(userEntity));

        UserJson result = userdataService.getUser(USER_NAME);

        Assertions.assertThat(result)
                .isNotNull()
                .extracting(
                        UserJson::id,
                        UserJson::username,
                        UserJson::firstname,
                        UserJson::lastname,
                        UserJson::avatar
                ).containsExactly(
                        userId,
                        USER_NAME,
                        FIRST_NAME,
                        LAST_NAME,
                        AVATAR
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "   ", "\t", "\n", " \t\n "})
    void getUserByUsernameShouldThrowExceptionForInvalidUsername(String invalidUsername) {
        final BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userdataService.getUser(invalidUsername));

        assertEquals("Username не должен быть пустой или содержать одни пробелы", exception.getMessage());
    }

    @Test
    void getUserByUsernameShouldThrowExceptionForNotFoundUsername() {
        final String nonExistentUsername = UUID.randomUUID().toString();
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userdataService.getUser(nonExistentUsername));

        assertEquals("Пользователь с username = '" + nonExistentUsername + "' не найден.", exception.getMessage());
    }

    @Test
    void fullUpdateUser() {

        final UserEntity userEntity2 = new UserEntity(
                userId,
                "Vika",
                "Vadimas",
                "Tolstova",
                AVATAR2.getBytes(StandardCharsets.UTF_8)
        );
        Mockito.when(userdataRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userdataRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(userEntity2);

        final UserJson result = userdataService.update(UserJson.fromEntity(userEntity2));

        Assertions.assertThat(result)
                .isNotNull()
                .extracting(
                        UserJson::id,
                        UserJson::username,
                        UserJson::firstname,
                        UserJson::lastname,
                        UserJson::avatar
                ).containsExactly(
                        userId,
                        userEntity2.getUsername(),
                        userEntity2.getFirstname(),
                        userEntity2.getLastname(),
                        AVATAR2
                );

        Mockito.verify(userdataRepository).save(ArgumentMatchers.any(UserEntity.class));
    }

    @Test
    void usernameUpdate() {
        final String updateUsername = "User";
        final UserEntity userEntity2 = new UserEntity(
                userId,
                updateUsername,
                null,
                null,
                null
        );

        final UserEntity actualUserEntity = new UserEntity(
                userId,
                updateUsername,
                userEntity.getFirstname(),
                userEntity.getLastname(),
                userEntity.getAvatar()
        );

        Mockito.when(userdataRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userdataRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(actualUserEntity);

        final UserJson result = userdataService.update(UserJson.fromEntity(userEntity2));

        Assertions.assertThat(result)
                .isNotNull()
                .extracting(
                        UserJson::id,
                        UserJson::username,
                        UserJson::firstname,
                        UserJson::lastname,
                        UserJson::avatar
                ).containsExactly(
                        userId,
                        actualUserEntity.getUsername(),
                        actualUserEntity.getFirstname(),
                        actualUserEntity.getLastname(),
                        AVATAR
                );

        Mockito.verify(userdataRepository).save(ArgumentMatchers.any(UserEntity.class));
    }

    @Test
    void avatarUpdate() {
        final UserEntity userEntity2 = new UserEntity(
                userId,
                null,
                null,
                null,
                AVATAR2.getBytes(StandardCharsets.UTF_8)
        );

        final UserEntity actualUserEntity = new UserEntity(
                userId,
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getLastname(),
                AVATAR2.getBytes(StandardCharsets.UTF_8)
        );

        Mockito.when(userdataRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        Mockito.when(userdataRepository.save(ArgumentMatchers.any(UserEntity.class))).thenReturn(actualUserEntity);

        final UserJson result = userdataService.update(UserJson.fromEntity(userEntity2));

        Assertions.assertThat(result)
                .isNotNull()
                .extracting(
                        UserJson::id,
                        UserJson::username,
                        UserJson::firstname,
                        UserJson::lastname,
                        UserJson::avatar
                ).containsExactly(
                        userId,
                        actualUserEntity.getUsername(),
                        actualUserEntity.getFirstname(),
                        actualUserEntity.getLastname(),
                        AVATAR2
                );

        Mockito.verify(userdataRepository).save(ArgumentMatchers.any(UserEntity.class));
    }

    @Test
    void updateUserShouldThrowExceptionForInvalidId() {
        final BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userdataService.update(new UserJson(null, null, null, null, null)));

        assertEquals("id: ID пользователя обязателен для обновления данных о пользователе", exception.getMessage());
        verify(userdataRepository, never()).findById(any());
        verify(userdataRepository, never()).save(any());
    }

    @Test
    void updateUserShouldThrowExceptionForNonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        UserJson userJson = new UserJson(
                nonExistentId, "testUser", "John", "Doe", "avatar"
        );

        when(userdataRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userdataService.update(userJson)
        );

        assertEquals("id: Пользователь не найден по id: " + nonExistentId, exception.getMessage());
        verify(userdataRepository).findById(nonExistentId);
        verify(userdataRepository, never()).save(any());
    }

    @Test
    void updateUserShouldThrowExceptionForExistingUsername() {
        final UUID otherUserId = UUID.randomUUID();

        final UserEntity existingUser = new UserEntity(
                userId,
                "existingUser",
                "John",
                "Doe",
                "avatar1".getBytes(StandardCharsets.UTF_8)
        );

        final UserEntity otherUser = new UserEntity(
                otherUserId,
                "occupiedUser",
                "Other",
                "User",
                new byte[0]
        );

        UserJson updateRequest = new UserJson(
                userId, "occupiedUser", "John", "Doe", "avatar"
        );

        when(userdataRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userdataRepository.findByUsername("occupiedUser")).thenReturn(Optional.of(otherUser));

        SameUsernameException exception = assertThrows(
                SameUsernameException.class,
                () -> userdataService.update(updateRequest)
        );

        assertEquals("username: Имя пользователя 'occupiedUser' уже занято.", exception.getMessage());
        verify(userdataRepository).findById(userId);
        verify(userdataRepository).findByUsername("occupiedUser");
        verify(userdataRepository, never()).save(any());
    }
}
