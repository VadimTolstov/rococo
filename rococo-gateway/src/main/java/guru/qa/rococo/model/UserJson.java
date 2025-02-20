package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,

        @NotNull(message = "username: Имя пользователя обязательно для заполнения")
        @Size(min = 3, max = 30, message = "username: Имя пользователя должно содержать от 3 до 30 символов")
        @JsonProperty("username")
        String username,

        @Size(max = 255, message = "firstname: Имя не может быть длиннее 255 символов")
        @JsonProperty("firstname")
        String firstname,

        @Size(max = 255, message = "lastname: Фамилия не может быть длиннее 255 символов")
        @JsonProperty("lastname")
        String lastname,

        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "avatar: Размер аватара не должен превышать 1MB")
        @JsonProperty("avatar")
        String avatar
) {
}