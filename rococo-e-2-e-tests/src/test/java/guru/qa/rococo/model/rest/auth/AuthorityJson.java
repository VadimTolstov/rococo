package guru.qa.rococo.model.rest.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.auth.Authority;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthorityJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("userId")
        AuthUserJson userId,
        @JsonProperty("authority")
        Authority authority) {
}
