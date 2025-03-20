package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @JsonProperty("city") String city,
        @JsonProperty("country") CountryJson country
) {
}