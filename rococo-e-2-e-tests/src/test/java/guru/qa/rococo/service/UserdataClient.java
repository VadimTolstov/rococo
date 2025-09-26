package guru.qa.rococo.service;


import guru.qa.rococo.model.rest.userdata.UserJson;
import lombok.NonNull;

public interface UserdataClient {

//    static UsersClient getInstance() {
//        return "api".equals(System.getProperty("client.impl"))
//                ? new UsersApiClient()
//                : new UsersDbClient();
//    }

    @NonNull
    UserJson createUser(@NonNull String username, @NonNull String password);

    @NonNull
    UserJson getUser(@NonNull String username);

    @NonNull
    UserJson updateUser(@NonNull UserJson userJson);

    @NonNull
    UserJson removeUser(@NonNull String username);
}
