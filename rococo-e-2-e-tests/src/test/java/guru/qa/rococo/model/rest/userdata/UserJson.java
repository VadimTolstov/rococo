package guru.qa.rococo.model.rest.userdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.userdata.UserEntity;


import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {
    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getAvatar() != null && entity.getAvatar().length > 0
                        ? new String(entity.getAvatar(), StandardCharsets.UTF_8)
                        : null
        );
    }

    public static @Nonnull UserEntity toEntity(@Nonnull UserJson json) {
        return new UserEntity(
                json.id,
                json.username,
                json.firstname,
                json.lastname,
                json.avatar.getBytes(StandardCharsets.UTF_8)
        );
    }
}