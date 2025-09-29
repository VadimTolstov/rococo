package guru.qa.rococo.api.core;

import guru.qa.rococo.ex.ApiException;
import lombok.NonNull;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Базовый интерфейс для API тестов с общими методами выполнения запросов.
 * Классы, реализующие этот интерфейс, получают доступ к default методам для выполнения HTTP запросов.
 */
public interface RequestExecutor {

    /**
     * Выполняет синхронный запрос и проверяет статус код
     */
    default @NonNull <T> T execute(@NonNull Call<T> call, int expectedStatusCode) {
        try {
            final Response<T> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
            return Objects.requireNonNull(
                    response.body(),
                    "Ответ API вернул null для " + call.request().method() + " " + call.request().url());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    /**
     * Выполняет запрос для получения списка (ожидает 200 OK)
     */
    default @NonNull <T> Response<T> executeForResponse(@NonNull Call<T> call) {
        try {
            final Response<T> response = call.execute();
            assertEquals(HttpStatus.SC_OK, response.code());
            return response;
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    /**
     * Выполняет void запрос (без возвращаемого тела)
     */
    default void executeVoid(@NonNull Call<Void> call, int expectedStatusCode) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    /**
     * Извлекает список из response или возвращает пустой список
     */
    default @NonNull <T> List<T> getList(@NonNull Response<List<T>> response) {
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
