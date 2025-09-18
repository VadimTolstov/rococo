package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.Authority;

import javax.annotation.Nonnull;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthorityJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("userId")
        AuthUserJson userId,
        @JsonProperty("authority")
        Authority authority) {

    private static @Nonnull AuthorityJson fromEntity(@Nonnull AuthorityEntity authority) {
        return new AuthorityJson(
                authority.getId(),
              AuthUserJson.fromEntity(authority.getUser()),
                authority.getAuthority()
        );
    }
}
