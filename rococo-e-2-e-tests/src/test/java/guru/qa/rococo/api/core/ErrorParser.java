package guru.qa.rococo.api.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.model.rest.ApiError;
import lombok.experimental.UtilityClass;
import okhttp3.ResponseBody;
import retrofit2.Response;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Objects;

@UtilityClass
class ErrorParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  static ApiError parseError(@Nullable Response<?> response) {
    Objects.requireNonNull(response, "Response не может быть null");

    ResponseBody errorBody = response.errorBody();
    if (errorBody == null) {
      throw new IllegalArgumentException("ErrorBody отсутствует в response");
    }

    String body;
    try {
      body = errorBody.string();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


    try {
      return MAPPER.readValue(body, ApiError.class);
    } catch (IOException e) {
      throw new RuntimeException("Ошибка парсинга errorBody в ApiError: " + body, e);
    }
  }
}

//package guru.qa.rococo.api.core;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import guru.qa.rococo.model.rest.ApiError;
//import lombok.experimental.UtilityClass;
//import okhttp3.ResponseBody;
//import retrofit2.Response;
//
//import javax.annotation.Nullable;
//import java.io.IOException;
//import java.time.Instant;
//import java.util.List;
//import java.util.Objects;
//
//@UtilityClass
//class ErrorParser {
//
//  private static final ObjectMapper MAPPER = new ObjectMapper();
//
//  static ApiError parseError(@Nullable Response<?> response) {
//    Objects.requireNonNull(response, "Response не может быть null");
//
//    ResponseBody errorBody = response.errorBody();
//    if (errorBody == null) {
//      throw new IllegalArgumentException("ErrorBody отсутствует в response");
//    }
//
//    String body;
//    try {
//      body = errorBody.string();
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//
//    // Проверяем что тело не пустое
//    if (body == null || body.trim().isEmpty()) {
//      return createFallbackApiError(response);
//    }
//
//    try {
//      // Настраиваем парсер для игнорирования неизвестных полей
//      MAPPER.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      return MAPPER.readValue(body, ApiError.class);
//    } catch (IOException e) {
//      throw new RuntimeException("Ошибка парсинга errorBody в ApiError: " + body, e);
//    }
//  }
//
//  /**
//   * Создает ApiError для случаев когда тело ошибки пустое
//   */
//  private static ApiError createFallbackApiError(Response<?> response) {
//    String path = response.raw().request().url().toString();
//    String domain = extractDomainFromPath(path);
//
//    return new ApiError(
//        "empty-error",
//        "Empty Response Body",
//        response.code(),
//        "Server returned error without details",
//        List.of("No error content provided in response body"),
//        Instant.now().toString(),
//        domain
//    );
//  }
//
//  /**
//   * Извлекает домен из пути URL
//   */
//  private static String extractDomainFromPath(String path) {
//    if (path == null) return "unknown";
//
//    if (path.contains("/api/artist")) return "artist";
//    if (path.contains("/api/museum")) return "museum";
//    if (path.contains("/api/painting")) return "painting";
//    if (path.contains("/api/user")) return "user";
//    if (path.contains("/oauth2") || path.contains("/login") || path.contains("/register")) return "auth";
//
//    return "general";
//  }
//}