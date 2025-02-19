package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Модель данных для представления запроса на создание или обновление картины.
 * <p>
 * Включает информацию о названии, описании, содержимом и связанных ресурсах.
 */
public record PaintingRequestJson(
        @JsonProperty("id")
        UUID id,

        @Size(min = 3, max = 255, message = "title: Название должно содержать от 3 до 255 символов")
        @NotBlank(message = "title: Название обязательно для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("title")
        String title,

        @Size(min = 11, max = 2000, message = "description: Описание должно содержать от 11 до 2000 символов")
        @NotBlank(message = "description: Описание обязательно для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("description")
        String description,

        @NotNull(message = "content: Фото обязательно для заполнения")
        @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "content: Размер фото не должен превышать 1MB")
        @JsonProperty("content")
        String content,

        @Valid
        @NotBlank(message = "artist: Художник обязателен для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("artist")
        ArtistRef artist,

        @Valid
        @NotBlank(message = "museum: Музей обязателен для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("museum")
        MuseumRef museum
) {
}