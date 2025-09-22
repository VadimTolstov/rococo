package guru.qa.rococo.model.rest.museum;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.entity.museum.CountryEntity;
import lombok.NonNull;


import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CountryJson(
        @JsonProperty("id") UUID id,
        @JsonProperty("name") String name
) {

    /**
     * Преобразует CountryEntity в CountryJson.
     *
     * @param entity Сущность CountryEntity.
     * @return Объект CountryJson.
     */
    public static @NonNull CountryJson fromEntity(@NonNull CountryEntity entity) {
        return new CountryJson(
                entity.getId(),
                entity.getName()
        );
    }

    /**
     * Преобразует CountryJson в CountryEntity.
     *
     * @return Объект CountryEntity.
     */
    public @NonNull CountryEntity toEntity() {
        CountryEntity entity = new CountryEntity();
        entity.setId(this.id);
        entity.setName(this.name);
        return entity;
    }
}