package guru.qa.rococo.api.geteway;

import guru.qa.rococo.model.rest.userdata.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

public interface UserDataGatewayApi {
  @GET("/api/user")
  Call<UserJson> getUser(@Header("Authorization") String bearerToken);

  @PATCH("/api/user")
  Call<UserJson> updateUser(@Body UserJson user,@Header("Authorization") String bearerToken);
}