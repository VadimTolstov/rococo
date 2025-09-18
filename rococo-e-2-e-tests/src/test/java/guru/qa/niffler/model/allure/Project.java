package guru.qa.niffler.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Project(
        @JsonProperty("id")
        String id
) {
}
