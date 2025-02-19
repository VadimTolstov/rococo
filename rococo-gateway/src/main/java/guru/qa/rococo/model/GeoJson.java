package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @Size(min = 3, max = 255, message = "city: Город должен содержать от 3 до 255 символов")
        @NotNull(message = "city: Город обязателен для заполнения")
        @JsonProperty("city")
        String city,

        @Valid
        @NotNull(message = "country: Страна обязательна для заполнения")
        @JsonProperty("country")
        CountryJson country
) {
}