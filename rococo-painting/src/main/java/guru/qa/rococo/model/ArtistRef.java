package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ArtistRef(
        @NotNull(message = "artist.id: ID художника обязателен для заполнения")
        @JsonProperty("id")
        UUID id
) {
}
