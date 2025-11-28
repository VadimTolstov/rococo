package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.api.RestUserDataClient;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class UserDataController {

    private static final Logger LOG = LoggerFactory.getLogger(UserDataController.class);
    private final RestUserDataClient restUserDataClient;

    @Autowired
    public UserDataController(RestUserDataClient restUserDataClient) {
        this.restUserDataClient = restUserDataClient;
    }

    @GetMapping
    public UserJson getUser(@AuthenticationPrincipal @Nonnull Jwt principal) {
        String username = principal.getClaim("sub");
        LOG.debug("Requesting current user data for: {}", username);
        return restUserDataClient.getUser(username);
    }

    @PatchMapping
    public UserJson updateUser(@AuthenticationPrincipal @Nonnull Jwt principal,
                               @Valid @RequestBody @Nonnull UserJson user) {
        String username = principal.getClaim("sub");
        LOG.info("Updating user profile for: {}", username);

        return restUserDataClient.updateUserInfo(
                user.addUsername(username)
        );
    }
}