package guru.qa.rococo.mapper.museum;

import guru.qa.rococo.data.entity.museum.GeoEntity;
import guru.qa.rococo.model.rest.museum.GeoJson;
import lombok.NonNull;

import java.util.ArrayList;

public class GeoMapper {

  /**
   * Преобразует GeoEntity в GeoJson.
   *
   * @param entity Сущность GeoEntity.
   * @return Объект GeoJson.
   */
  public static @NonNull GeoJson mapToJson(@NonNull GeoEntity entity) {
    return new GeoJson(
        entity.getCity(),
        CountryMapper.mapToJson(entity.getCountry())
    );
  }

  /**
   * Преобразует GeoJson в GeoEntity.
   *
   * @return Объект GeoEntity.
   */
  public static @NonNull GeoEntity mapToEntity(@NonNull GeoJson json) {
    return new GeoEntity(
        null,
        json.city(),
        CountryMapper.mapToEntity(json.country()),
        new ArrayList<>()
    );
  }
}
