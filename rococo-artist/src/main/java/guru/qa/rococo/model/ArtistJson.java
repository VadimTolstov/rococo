package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.ArtistEntity;

import jakarta.annotation.Nonnull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo
) {

    public static @Nonnull ArtistJson fromEntity(@Nonnull ArtistEntity entity) {
        return new ArtistJson(
                entity.getId(),
                entity.getName(),
                entity.getBiography(),
                entity.getPhoto() != null && entity.getPhoto().length > 0
                        ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
                        : null
        );
    }

    public @Nonnull ArtistEntity toEntity() {
        ArtistEntity entity = new ArtistEntity();
        entity.setId(this.id);
        entity.setName(this.name.trim());
        entity.setBiography(this.biography.trim());
        entity.setPhoto(
                this.photo != null && this.photo.startsWith("data:image")
                        ? this.photo.getBytes(StandardCharsets.UTF_8)
                        : null
        );
        return entity;
    }
}