package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.data.CountryEntity;

import jakarta.annotation.Nonnull;

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
    public static @Nonnull CountryJson fromEntity(@Nonnull CountryEntity entity) {
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
    public @Nonnull CountryEntity toEntity() {
        CountryEntity entity = new CountryEntity();
        entity.setId(this.id);
        entity.setName(this.name);
        return entity;
    }
}