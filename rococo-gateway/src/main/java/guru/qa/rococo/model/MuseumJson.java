package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MuseumJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 255, message = "Название музея должно содержать от 3 до 255 символов")
        @NotBlank(message = "Название музея обязательно для заполнения, не может быть пустым или состоять только из пробелов")
        @JsonProperty("title")
        String title,

        @Size(min = 10, max = 2000, message = "Описание музея должно должно содержать от 10 до 2000 символов")
        @NotBlank(message = "Описание музея обязательно для заполнения, не может быть пустым или состоять только из пробелов")
        @JsonProperty("description")
        String description,

        @Pattern(regexp = "^data:image/.*", message = "Изображение музея: Фото должно начинаться с 'data:image/'")
        @NotNull(message = "Изображение музея: Изображение музея обязательно для заполнения")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Изображение музея: Размер фото не должен превышать 1MB")
        @JsonProperty("photo")
        String photo,

        @Valid
        @NotNull(message = "geo: Геоданные обязательны для заполнения")
        @JsonProperty("geo")
        GeoJson geo
) {
        public MuseumJson(UUID id, String title, String description, String photo, GeoJson geo) {
                this.id = id;
                this.title = normalizeString(title);
                this.description = normalizeString(description);
                this.photo = photo;
                this.geo = geo;
        }

        private static String normalizeString(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? " " : trimmed;
        }
}