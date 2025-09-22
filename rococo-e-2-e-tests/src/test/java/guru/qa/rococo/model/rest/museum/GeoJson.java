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

    /**
     * Преобразует GeoEntity в GeoJson.
     *
     * @param entity Сущность GeoEntity.
     * @return Объект GeoJson.
     */
    public static @NonNull GeoJson fromEntity(@NonNull GeoEntity entity) {
        return new GeoJson(
                entity.getCity(),
                CountryJson.fromEntity(entity.getCountry())
        );
    }

    /**
     * Преобразует GeoJson в GeoEntity.
     *
     * @return Объект GeoEntity.
     */
    public @NonNull GeoEntity toEntity() {
        GeoEntity entity = new GeoEntity();
        entity.setCity(city);
        entity.setCountry(country.toEntity());
        return entity;
    }
}
