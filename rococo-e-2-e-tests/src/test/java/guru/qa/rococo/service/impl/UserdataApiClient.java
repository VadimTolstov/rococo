package guru.qa.rococo.service.impl;

import guru.qa.rococo.api.UserdataApi;
import guru.qa.rococo.api.core.ApiTestBase;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.UserdataClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;

public class UserdataApiClient extends ApiTestBase implements UserdataClient {
    private static final Config CFG = Config.getInstance();

    private final UserdataApi userdataApi;

    public UserdataApiClient() {
        userdataApi = new RestClient.EmtyRestClient(
                CFG.userdataUrl(),
                HttpLoggingInterceptor.Level.BODY
        ).create(UserdataApi.class);
    }

    @Override //todo подумать над контроллером для добавления
    public @NonNull UserJson createUser(@NonNull String username, @NonNull String password) {
        return null;
    }

    /**
     * Метод для получения пользователя по имени.
     *
     * @param username Имя пользователя (тип: {@link String}).
     * @return {@link UserJson}.
     */
    @Step("Получения пользователя  по username = {username}")
    @Override
    public @NonNull UserJson getUser(@NonNull String username) {
        return execute(userdataApi.getUser(username), HttpStatus.SC_OK);
    }

    /**
     * Метод для обновления данных пользователя.
     *
     * @param userJson данные пользователя (тип: {@link UserJson}).
     * @return {@link UserJson}.
     */
    @Step("Обновление данных пользователя")
    @Override
    public @NonNull UserJson updateUser(@NonNull UserJson userJson) {
        return execute(userdataApi.updateUser(userJson), HttpStatus.SC_OK);
    }

    @Override //todo добавить контроллер на удаление
    public @NonNull UserJson removeUser(@NonNull String username) {
        return null;
    }
}
