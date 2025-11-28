package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Модель данных для представления художника.
 * <p>
 * Содержит информацию об идентификаторе, имени, биографии и фотографии художника.
 * Поддерживает валидацию входных данных и нормализацию строк перед сохранением.
 * </p>
 */
public record ArtistJson(

        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 255, message = "name: Имя должно содержать от 3 до 255 символов")
        @NotBlank(message = "name: Имя обязательно для заполнения, не может быть пустым или состоять только из пробелов")
        @JsonProperty("name")
        String name,

        @Size(min = 10, max = 2000, message = "biography: Биография должна содержать от 10 до 2000 символов")
        @NotBlank(message = "biography: Биография обязательна для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("biography")
        String biography,

        @Pattern(regexp = "^data:image/.*", message = "photo: Фото должно начинаться с 'data:image/'")
        @NotNull(message = "photo: Фото обязательно для заполнения")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "photo: Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo) {

    @JsonCreator
    public ArtistJson(
            @JsonProperty("id") UUID id,
            @JsonProperty("name") String name,
            @JsonProperty("biography") String biography,
            @JsonProperty("photo") String photo) {
        this.id = id;
        this.name = normalizeString(name);
        this.biography = normalizeString(biography);
        this.photo = photo;
    }

    private static String normalizeString(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? " " : trimmed;
    }
}