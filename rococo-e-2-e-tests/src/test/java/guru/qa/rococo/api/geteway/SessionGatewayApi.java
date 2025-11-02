package guru.qa.rococo.api.geteway;

import guru.qa.rococo.model.rest.SessionJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SessionGatewayApi {

  @GET("/api/session")
  Call<SessionJson> getSession(@Header("Authorization") String bearerToken);
}