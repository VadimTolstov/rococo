package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MuseumRef(
        @NotNull(message = "museum.id: ID музея обязателен для заполнения")
        @JsonProperty("id")
        UUID id
) {
}
