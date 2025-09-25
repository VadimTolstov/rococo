package guru.qa.rococo;

import guru.qa.rococo.model.rest.userdata.UserJson;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Query;

public interface UserdataApi {

    @GET("/internal/user")
    Call<UserJson> getUser(@Query("username") @NonNull String username);

    @PATCH("/internal/user")
    Call<UserJson> patchUser(@Body @NonNull UserJson user);
}
