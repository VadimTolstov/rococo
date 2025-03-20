package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaintingResponseJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("artist")
        ArtistJson artist,
        @JsonProperty("museum")
        MuseumJson museum
) {

    /**
     * Преобразует PaintingEntity в PaintingJson.
     *
     * @param entity Сущность PaintingEntity.
     * @return Объект PaintingJson.
     */
    public static @Nonnull PaintingResponseJson fromEntity(@Nonnull PaintingEntity entity,
                                                           @Nonnull ArtistJson artist,
                                                           @Nonnull MuseumJson museum) {
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

//    /** todo стереть если не нужно
//     * Преобразует PaintingJson в PaintingEntity.
//     *
//     * @return Объект PaintingEntity.
//     */
//    public @Nonnull PaintingEntity toEntity() {
//        PaintingEntity entity = new PaintingEntity();
//        entity.setId(this.id);
//        entity.setTitle(this.title);
//        entity.setDescription(this.description);
//        entity.setArtist(this.artist != null ? this.artist.toEntity() : null);
//        entity.setMuseum(this.museum != null ? this.museum.toEntity() : null);
//        entity.setContent(this.content != null ? this.content.getBytes(StandardCharsets.UTF_8) : null);
//        return entity;
//    }
}