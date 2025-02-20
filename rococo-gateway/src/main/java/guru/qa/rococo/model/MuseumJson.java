package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        @Size(min = 10, max = 2000, message = "Описание музея должно должно содержать от 3 до 2000 символов")
        @NotBlank(message = "Описание музея обязательно для заполнения, не может быть пустым или состоять только из пробелов")
        @JsonProperty("description")
        String description,

        @Size(min = 3, max = 255, message = "Имя города должно содержать от 3 до 255 символов")
        @NotBlank(message = "Имя города обязательно для заполнения, не может быть пустым или состоять только из пробелов")
        @JsonProperty("city")
        String city,

        @Pattern(regexp = "^data:image/.*", message = "photo: Фото должно начинаться с 'data:image/'")
        @NotNull(message = "photo: Изображение музея обязательно для заполнения")
        @JsonProperty("photo")
        String photo,

        @NotNull(message = "Необходимо выбрать страну")
        @JsonProperty("country")
        CountryJson country
        //todo  проверить если в запросе геоданные
        //        @Valid
        //        @NotNull(message = "geo: Геоданные обязательны для заполнения")
        //        @JsonProperty("geo")
        //        GeoJson geo
) {
}