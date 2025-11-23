package guru.qa.rococo.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.qameta.allure.Param;
import io.qameta.allure.model.Parameter;

public record AllureResult(
        @JsonProperty("file_name")
        String filename,

        @JsonProperty("content_base64")
        @Param(mode = Parameter.Mode.MASKED)
        String contentBase64
) {
}
