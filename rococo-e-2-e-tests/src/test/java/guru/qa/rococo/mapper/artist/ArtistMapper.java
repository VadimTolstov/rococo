package guru.qa.rococo.mapper.artist;

import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public class ArtistMapper {

  /**
   * Преобразует ArtistEntity в ArtistJson.
   *
   * @param entity Сущность ArtistEntity.
   * @return Объект ArtistJson.
   */
  public static @NonNull ArtistJson mapToJson(@NonNull ArtistEntity entity) {
    return new ArtistJson(
        entity.getId(),
        entity.getName(),
        entity.getBiography(),
        entity.getPhoto() != null && entity.getPhoto().length > 0
            ? new String(entity.getPhoto(), StandardCharsets.UTF_8)
            : null
    );
  }

  /**
   * Преобразует ArtistJson в ArtistEntity.
   *
   * @param json Сущность ArtistJson.
   * @return Объект ArtistEntity.
   */
  public static @NonNull ArtistEntity mapToEntity(@NonNull ArtistJson json) {
    return new ArtistEntity(
        json.id(),
        json.name(),
        json.biography(),
        json.photo() != null ? json.photo().getBytes(StandardCharsets.UTF_8) : null
    );
  }
}
