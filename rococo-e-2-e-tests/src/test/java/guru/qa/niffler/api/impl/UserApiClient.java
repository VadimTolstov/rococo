package guru.qa.niffler.api.impl;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.ex.ApiException;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient extends RestClient {

    private final UserApi userApi;

    public UserApiClient() {
        super(CFG.userdataUrl());
        userApi = retrofit.create(UserApi.class);
    }

    @Step("Send GET [/internal/users/current] to niffler-userdata")
    public @Nonnull UserJson currentUser(@Nonnull String username) {
        return execute(userApi.currentUser(username), HttpStatus.SC_OK);
    }

    @Step("Send GET [/internal/users/all] to niffler-userdata")
    public @Nonnull List<UserJson> allUsers(@Nonnull String username,
                                            @Nullable String searchQuery) {
        return getList(executeForList(userApi.allUsers(username, searchQuery)));
    }

    @Step("Send POST [/internal/users/update] to niffler-userdata")
    public @Nonnull UserJson updateUserInfo(@Nonnull UserJson user) {
        return execute(userApi.updateUserInfo(user), HttpStatus.SC_OK);
    }

    @Step("Send GET [/internal/friends/all] to niffler-userdata")
    public @Nonnull List<UserJson> friends(@Nonnull String username,
                                           @Nullable String searchQuery) {
        return getList(executeForList(userApi.friends(username, searchQuery)));
    }

    @Step("Send DELETE [/internal/friends/remove] to niffler-userdata")
    public void removeFriend(@Nonnull String username,
                             @Nonnull String targetUsername) {
        executeVoid(userApi.removeFriend(username, targetUsername));
    }

    @Step("Send POST [/internal/invitations/send] to niffler-userdata")
    public @Nonnull UserJson sendInvitation(@Nonnull String username,
                                            @Nonnull String targetUsername) {
        return execute(userApi.sendInvitation(username, targetUsername), HttpStatus.SC_OK);
    }


    @Step("Send POST [/internal/invitations/accept] to niffler-userdata")
    public @Nonnull UserJson acceptInvitation(@Nonnull String username,
                                              @Nonnull String targetUsername) {
        return execute(userApi.acceptInvitation(username, targetUsername), HttpStatus.SC_OK);
    }

    @Step("Send POST [/internal/invitations/decline] to niffler-userdata")
    public @Nonnull UserJson declineInvitation(@Nonnull String username,
                                               @Nonnull String targetUsername) {
        return execute(userApi.declineInvitation(username, targetUsername), HttpStatus.SC_OK);
    }

    // Общие методы для выполнения запросов
    private <T> T execute(Call<T> call, int expectedStatusCode) {
        try {
            final Response<T> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
            return Objects.requireNonNull(
                    response.body(),
                    "Ответ API вернул null для " + call.request().method() + " " + call.request().url());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private <T> Response<T> executeForList(Call<T> call) {
        try {
            final Response<T> response = call.execute();
            assertEquals(HttpStatus.SC_OK, response.code());
            return response;
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private void executeVoid(Call<Void> call) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(HttpStatus.SC_ACCEPTED, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private <T> List<T> getList(Response<List<T>> response) {
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
