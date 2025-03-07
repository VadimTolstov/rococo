//package guru.qa.rococo.model.mm;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import jakarta.annotation.Nonnull;
//import guru.qa.rococo.data.PaintingEntity;
//
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public record PaintingJson(
//        @JsonProperty("id")
//        UUID id,
//        @JsonProperty("title")
//        String title,
//        @JsonProperty("description")
//        String description,
//        @JsonProperty("artist")
//        ArtistJson artist,
//        @JsonProperty("museum")
//        MuseumJson museum,
//        @JsonProperty("content")
//        String content
//) {
//
//    /**
//     * Преобразует PaintingEntity в PaintingJson.
//     *
//     * @param entity Сущность PaintingEntity.
//     * @return Объект PaintingJson.
//     */
//    public static @Nonnull PaintingJson fromEntity(@Nonnull PaintingEntity entity) {
//        return new PaintingJson(
//                entity.getId(),
//                entity.getTitle(),
//                entity.getDescription(),
//                ArtistJson.fromEntity(entity.getArtist()),
//                entity.getMuseum() != null ? MuseumJson.fromEntity(entity.getMuseum()) : null,
//                entity.getContent() != null && entity.getContent().length > 0
//                        ? new String(entity.getContent(), StandardCharsets.UTF_8)
//                        : null
//        );
//    }
//
//    /**
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
//}