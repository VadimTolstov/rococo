package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.MuseumEntity;

import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuseumJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("photo") String photo,
        @JsonProperty("geo") GeoJson geo
) {

    public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
        return new MuseumJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null && entity.getPhoto().length > 0
                        ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
                        : null,
                GeoJson.fromEntity(entity.getGeo())
        );
    }

    public @Nonnull MuseumEntity toEntity() {
        MuseumEntity entity = new MuseumEntity();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setDescription(this.description);
        entity.setPhoto(this.photo != null ? this.photo.getBytes(StandardCharsets.UTF_8) : null);
        entity.setGeo(this.geo != null ? this.geo.toEntity() : null);
        return entity;
    }
}