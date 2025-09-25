package guru.qa.rococo.service.impl;


import guru.qa.rococo.model.rest.userdata.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UsersClient {

    static UsersClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new UsersApiClient()
                : new UsersDbClient();
    }

    @Nonnull
    UserJson createUser(String username, String password);

    @Nonnull
    UserJson removeUser(String username);
}
