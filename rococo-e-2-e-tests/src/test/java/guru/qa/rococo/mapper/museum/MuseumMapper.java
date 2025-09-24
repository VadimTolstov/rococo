package guru.qa.rococo.mapper.museum;

import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public class MuseumMapper {

    /**
     * Преобразует MuseumEntity в MuseumJson.
     *
     * @param entity Сущность MuseumEntity.
     * @return Объект MuseumJson.
     */
    public @NonNull MuseumJson toJson(@NonNull MuseumEntity entity) {
        return new MuseumJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null && entity.getPhoto().length > 0
                        ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
                        : null,
                new GeoMapper().toJson(entity.getGeo())
        );
    }

    /**
     * Преобразует MuseumJson в MuseumEntity.
     *
     * @param json Сущность MuseumJson.
     * @return Объект MuseumEntity.
     */
    public @NonNull MuseumEntity toEntity(@NonNull MuseumJson json) {
        return new MuseumEntity(
                json.id(),
                json.title(),
                json.description(),
                json.photo() != null ? json.photo().getBytes(StandardCharsets.UTF_8) : null,
                json.geo() != null ? new GeoMapper().toEntity(json.geo()) : null
        );
    }
}
