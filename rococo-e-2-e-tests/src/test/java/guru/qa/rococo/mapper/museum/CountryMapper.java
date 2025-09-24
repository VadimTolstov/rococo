package guru.qa.rococo.mapper.museum;

import guru.qa.rococo.data.entity.museum.CountryEntity;
import guru.qa.rococo.model.rest.museum.CountryJson;
import lombok.NonNull;

import java.util.ArrayList;

public class CountryMapper {

    /**
     * Преобразует CountryEntity в CountryJson.
     *
     * @param entity Сущность CountryEntity.
     * @return Объект CountryJson.
     */
    public @NonNull CountryJson toJson(@NonNull CountryEntity entity) {
        return new CountryJson(
                entity.getId(),
                entity.getName()
        );
    }

    /**
     * Преобразует CountryJson в CountryEntity.
     *
     * @return Объект CountryEntity.
     */
    public @NonNull CountryEntity toEntity(@NonNull CountryJson json) {
        return new CountryEntity(
                json.id(),
                json.name(),
                new ArrayList<>()
        );
    }
}
