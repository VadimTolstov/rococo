package guru.qa.rococo.service.api.gateway;

import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.gateway.SessionGatewayApi;
import guru.qa.rococo.api.gateway.UserDataGatewayApi;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.rest.SessionJson;
import guru.qa.rococo.model.rest.userdata.UserJson;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;

public class SessionGatewayApiClient implements RequestExecutor {
  private static final Config CFG = Config.getInstance();

  private final SessionGatewayApi userdataApi;

  public SessionGatewayApiClient() {
    userdataApi = new RestClient.EmtyRestClient(
        CFG.gatewayUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(SessionGatewayApi.class);
  }

  /**
   * Метод для получения сеанса пользователя по токину.
   *
   * @param bearerToken объект {@link String} с токином авторизации.
   * @return {@link UserJson}.
   */
  @Step("Получения пользователя  по username = {username}")
  public @NonNull SessionJson getSession(@NonNull String bearerToken, int statusCode) {
    return execute(userdataApi.getSession(bearerToken), statusCode);
  }
}
