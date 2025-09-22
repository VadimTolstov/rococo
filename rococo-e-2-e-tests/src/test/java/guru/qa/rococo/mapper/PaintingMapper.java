package guru.qa.rococo.mapper;

import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public class PaintingMapper {


    /**
     * Преобразует PaintingEntity в PaintingJson.
     *
     * @param entity Сущность PaintingEntity.
     * @return Объект PaintingJson.
     */
    public @NonNull PaintingJson toJson(@NonNull PaintingEntity entity, @NonNull ArtistJson artist, @NonNull MuseumJson museum) {
        return new PaintingJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null && entity.getContent().length > 0
                        ? new String(entity.getContent(), StandardCharsets.UTF_8)
                        : null,
                artist,
                museum
        );
    }

    /**
     * Преобразует PaintingJson в PaintingEntity.
     *
     * @param json Сущность PaintingJson.
     * @return Объект PaintingEntity.
     */
    public @NonNull PaintingEntity toEntity(@NonNull PaintingJson json) {
        return new PaintingEntity(
                json.id(),
                json.title(),
                json.description(),
                json.artist() != null ? json.artist().id() : null,
                json.museum() != null ? json.museum().id() : null,
                json.content() != null ? json.content().getBytes(StandardCharsets.UTF_8) : null
        );
    }
}