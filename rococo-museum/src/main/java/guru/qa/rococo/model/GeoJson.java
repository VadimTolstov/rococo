package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeoJson(
    @JsonProperty("id") UUID id,
    @JsonProperty("city") String city,
    @JsonProperty("country") CountryJson country
) {
}
