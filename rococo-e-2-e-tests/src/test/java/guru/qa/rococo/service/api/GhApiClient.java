package guru.qa.rococo.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rococo.api.GhApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nonnull;

public class GhApiClient implements RequestExecutor {
  private static final Config CFG = Config.getInstance();
  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";
  private final GhApi ghApi;

  public GhApiClient() {
    ghApi = new RestClient.EmtyRestClient(
        CFG.ghUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(GhApi.class);
  }

  public @Nonnull String issueState(@Nonnull String issueNumber) {
    final JsonNode execute = execute(
        ghApi.issue("Bearer " + System.getenv(GH_TOKEN_ENV), issueNumber),
        200);
    return execute.get("state").asText();
  }
}