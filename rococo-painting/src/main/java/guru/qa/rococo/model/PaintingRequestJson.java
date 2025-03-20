package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PaintingRequestJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("artist")
        ArtistRef artist,
        @JsonProperty("museum")
        MuseumRef museum
) {
    /**
     * Преобразует PaintingEntity в PaintingJson.
     *
     * @param entity Сущность PaintingEntity.
     * @return Объект PaintingJson.
     */
    public static @Nonnull PaintingRequestJson fromEntity(@Nonnull PaintingEntity entity) {
        return new PaintingRequestJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getContent() != null && entity.getContent().length > 0
                        ? new String(entity.getContent(), StandardCharsets.UTF_8)
                        : null,
                new ArtistRef(entity.getArtist()),
                new MuseumRef(entity.getMuseum())
        );
    }
}