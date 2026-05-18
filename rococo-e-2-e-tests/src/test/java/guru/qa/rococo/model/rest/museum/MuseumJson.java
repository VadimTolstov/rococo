package guru.qa.rococo.model.rest.museum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuseumJson(
    @JsonProperty("id") UUID id,
    @JsonProperty("title") String title,
    @JsonProperty("description") String description,
    @JsonProperty("photo") String photo,
    @JsonProperty("geo") GeoJson geo
) {
}