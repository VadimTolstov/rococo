package guru.qa.rococo.mapper;


import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.PaintingResponseJson;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
//todo сделать маппер убрав методы из DTO
@Component
public class PaintingMapper {

    public PaintingResponseJson toJson(PaintingEntity entity, ArtistJson artist, MuseumJson museum) {
        return new PaintingResponseJson(
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

    public PaintingEntity toEntity(PaintingResponseJson json) {
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