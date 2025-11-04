package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

/**
 * Модель для представления ошибки API в формате Spring Boot Error.
 * Соответствует структуре JSON ответа об ошибке от сервера Rococo.
 *
 * @param type      тип ошибки (например, "rococo-artist: Not Found")
 * @param title     заголовок ошибки (например, "Not Found")
 * @param status    HTTP статус код (например, 404)
 * @param detail    детальное описание ошибки
 * @param errors    список дополнительных сообщений об ошибках
 * @param timestamp временная метка возникновения ошибки
 * @param path      путь API где произошла ошибка
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiError(
    @JsonProperty("type") String type,
    @JsonProperty("title") String title,
    @JsonProperty("status") String status,
    @JsonProperty("detail") String detail,
    @JsonProperty("errors") List<String> errors,
    @JsonProperty("timestamp") String timestamp,
    @JsonProperty("path") String path
) implements Serializable {

  public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize ApiError", e);
    }
  }

  public String remoteError() {
    return "Remote error: "+this.toJson();
  }
}