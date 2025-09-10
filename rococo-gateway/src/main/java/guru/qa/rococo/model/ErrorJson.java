package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorJson(
        @JsonProperty("type")
        String type,

        @JsonProperty("title")
        String title,

        @JsonProperty("status")
        Integer status,

        @JsonProperty("detail")
        String detail,

        @JsonProperty("errors")
        List<String> errors,

        @JsonProperty("timestamp")
        String timestamp,

        @JsonProperty("path")
        String path
) {
    // Конструкторы для обратной совместимости
    public ErrorJson(String type, String title, Integer status, String detail, String path) {
        this(type, title, status, detail, List.of(detail), LocalDateTime.now().toString(), path);
    }

    public ErrorJson(String type, String title, Integer status, String detail,
                     List<String> errors, String timestamp, String path) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.errors = errors;
        this.timestamp = timestamp;
        this.path = path;
    }
}