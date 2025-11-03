package guru.qa.rococo.service.api.gateway;

import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.gateway.UserDataGatewayApi;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.rest.userdata.UserJson;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;

public class UserdataGatewayApiClient implements RequestExecutor {
  private static final Config CFG = Config.getInstance();

  private final UserDataGatewayApi userdataApi;

  public UserdataGatewayApiClient() {
    userdataApi = new RestClient.EmtyRestClient(
        CFG.gatewayUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(UserDataGatewayApi.class);
  }

  /**
   * Метод для получения пользователя по имени.
   *
   * @param bearerToken объект {@link String} с токином авторизации.
   * @return {@link UserJson}.
   */
  @Step("Получения пользователя  по username = {username}")
  public @NonNull UserJson getUser(@NonNull String bearerToken, int statusCode) {
    return execute(userdataApi.getUser(bearerToken), statusCode);
  }

  /**
   * Метод для обновления данных пользователя.
   *
   * @param userJson    данные пользователя (тип: {@link UserJson}).
   * @param bearerToken объект {@link String} с токином авторизации.
   * @return {@link UserJson}.
   */
  @Step("Обновление данных пользователя")
  public @NonNull UserJson updateUser(@NonNull UserJson userJson, @NonNull String bearerToken, int statusCode) {
    return execute(userdataApi.updateUser(userJson, bearerToken), statusCode);
  }
}
