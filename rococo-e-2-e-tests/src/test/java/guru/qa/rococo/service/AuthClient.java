package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.userdata.UserJson;
import lombok.NonNull;

public interface AuthClient {

    @NonNull
    UserJson createUser(@NonNull String username, @NonNull String password);
}
