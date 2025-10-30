package guru.qa.rococo.api;

import guru.qa.rococo.model.rest.userdata.UserJson;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.*;

public interface UserdataApi {

    @GET("/internal/user")
    Call<UserJson> getUser(@Query("username") @NonNull String username);

    @PATCH("/internal/user")
    Call<UserJson> updateUser(@Body @NonNull UserJson user);
}
