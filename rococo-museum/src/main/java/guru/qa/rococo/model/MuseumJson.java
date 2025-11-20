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

  /**
   * Преобразует MuseumEntity в MuseumJson.
   *
   * @param entity Сущность MuseumEntity.
   * @return Объект MuseumJson.
   */
  public static @Nonnull MuseumJson fromEntity(@Nonnull MuseumEntity entity) {
    return new MuseumJson(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getPhoto() != null && entity.getPhoto().length > 0
            ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
            : null,
        new GeoJson(entity.getGeo(), null, null)
        );
  }
}