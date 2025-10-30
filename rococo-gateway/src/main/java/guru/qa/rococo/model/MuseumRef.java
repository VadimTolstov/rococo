package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MuseumRef(
        @NotNull(message = "Укажите, где хранится оригинал - обязателен для заполнения")
        @JsonProperty("id")
        UUID id
) {
}
