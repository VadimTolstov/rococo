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
 * Базовый класс для API тестов с общими методами выполнения запросов.
 * Наследники получают доступ к protected методам для выполнения HTTP запросов.
 */
public abstract class ApiTestBase {

    // Общие методы для выполнения запросов
    protected final @NonNull <T> T execute(@NonNull Call<T> call, int expectedStatusCode) {
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

    protected final <T> Response<T> executeForList(@NonNull Call<T> call) {
        try {
            final Response<T> response = call.execute();
            assertEquals(HttpStatus.SC_OK, response.code());
            return response;
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }

    }

    protected final void executeVoid(@NonNull Call<Void> call, int expectedStatusCode) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    protected final @NonNull <T> List<T> getList(@NonNull Response<List<T>> response) {
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
