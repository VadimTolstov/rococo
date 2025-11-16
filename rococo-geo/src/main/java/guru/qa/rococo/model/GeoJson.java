package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.GeoEntity;
import jakarta.annotation.Nonnull;

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
    public static @Nonnull GeoJson fromEntity(@Nonnull GeoEntity entity) {
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
    public @Nonnull GeoEntity toEntity() {
        GeoEntity entity = new GeoEntity();
        entity.setCity(city);
        entity.setCountry(country.toEntity());
        return entity;
    }
}
