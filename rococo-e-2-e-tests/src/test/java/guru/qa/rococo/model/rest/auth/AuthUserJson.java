package guru.qa.rococo.model.rest.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthUserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("password")
        String password,
        @JsonProperty("enabled")
        Boolean enabled,
        @JsonProperty("accountNonExpired")
        Boolean accountNonExpired,
        @JsonProperty("accountNonLocked")
        Boolean accountNonLocked,
        @JsonProperty("credentialsNonExpired")
        Boolean credentialsNonExpired) {

}