package guru.qa.rococo.mapper.museum;

import guru.qa.rococo.data.entity.museum.GeoEntity;
import guru.qa.rococo.model.rest.museum.GeoJson;
import lombok.NonNull;

import java.util.ArrayList;

public class GeoMapper {

    /**
     * Преобразует GeoEntity в GeoJson.
     *
     * @param entity Сущность GeoEntity.
     * @return Объект GeoJson.
     */
    public @NonNull GeoJson toJson(@NonNull GeoEntity entity) {
        return new GeoJson(
                entity.getCity(),
                new CountryMapper().toJson(entity.getCountry())
        );
    }

    /**
     * Преобразует GeoJson в GeoEntity.
     *
     * @return Объект GeoEntity.
     */
    public @NonNull GeoEntity toEntity(@NonNull GeoJson json) {
        return new GeoEntity(
                null,
                json.city(),
                new CountryMapper().toEntity(json.country()),
                new ArrayList<>()
        );
    }
}
