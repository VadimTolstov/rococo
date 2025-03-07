//package guru.qa.rococo.model.mm;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import guru.qa.rococo.data.MuseumEntity;
//
//import jakarta.annotation.Nonnull;
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public record MuseumJson(
//        @JsonProperty("id")
//        UUID id,
//        @JsonProperty("title")
//        String title,
//        @JsonProperty("description")
//        String description,
//        @JsonProperty("city")
//        String city,
//        @JsonProperty("photo")
//        String photo,
//        @JsonProperty("country")
//        CountryJson country
//) {
//
//    /**
//     * Преобразует MuseumEntity в MuseumJson.
//     *
//     * @param entity Сущность MuseumEntity.
//     * @return Объект MuseumJson.
//     */
//    public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
//        return new MuseumJson(
//                entity.getId(),
//                entity.getTitle(),
//                entity.getDescription(),
//                entity.getCity(),
//                entity.getPhoto() != null && entity.getPhoto().length > 0
//                        ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
//                        : null,
//                CountryJson.fromEntity(entity.getCountry())
//        );
//    }
//
//    /**
//     * Преобразует MuseumJson в MuseumEntity.
//     *
//     * @return Объект MuseumEntity.
//     */
//    public @Nonnull MuseumEntity toEntity() {
//        MuseumEntity entity = new MuseumEntity();
//        entity.setId(this.id);
//        entity.setTitle(this.title);
//        entity.setDescription(this.description);
//        entity.setCity(this.city);
//        entity.setPhoto(this.photo != null ? this.photo.getBytes(StandardCharsets.UTF_8) : null);
//        entity.setCountry(this.country != null ? this.country.toEntity() : null);
//        return entity;
//    }
//}