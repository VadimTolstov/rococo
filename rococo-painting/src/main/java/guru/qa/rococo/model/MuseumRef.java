package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MuseumRef(
        @JsonProperty("id")
        UUID id
) {
}
