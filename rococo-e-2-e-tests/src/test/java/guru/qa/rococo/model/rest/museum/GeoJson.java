package guru.qa.rococo.model.rest.museum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.museum.GeoEntity;
import lombok.NonNull;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record GeoJson(
        @JsonProperty("city") String city,
        @JsonProperty("country") CountryJson country
) {
}
