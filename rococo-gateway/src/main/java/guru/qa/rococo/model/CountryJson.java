package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CountryJson(
        @NotBlank(message = "country.id: ID страны обязателен для заполнения")
        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        String name
) {
}