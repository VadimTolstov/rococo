package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @Size(min = 3, max = 255, message = "city: Город должен содержать от 3 до 255 символов")
        @NotBlank(message = "city: Город обязательна для заполнения, не может быть пустой или состоять только из пробелов")
        @JsonProperty("city")
        String city,

        @Valid
        @NotNull(message = "country: Страна обязательна для заполнения")
        @JsonProperty("country")
        CountryJson country
) {

        public GeoJson(String city, CountryJson country) {
                this.city = normalizeString(city);
                this.country = country;
        }

        private static String normalizeString(String value) {
                if (value == null) {
                        return null;
                }
                String trimmed = value.trim();
                return trimmed.isEmpty() ? " " : trimmed;
        }
}