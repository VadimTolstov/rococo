package guru.qa.niffler.api.core.interceptor;

import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Интерцептор для обработки HTTP-ответов и извлечения кода авторизации из заголовков перенаправления.
 * Этот интерцептор используется для перехвата ответов, содержащих перенаправление (redirect),
 * и извлечения значения кода авторизации из заголовка "Location".
 * Извлеченный код авторизации сохраняется в расширении {@link ApiLoginExtension}.
 */
public class CodeInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {

        /*
         * Пример модификации запроса:
         * Можно изменить запрос, добавив в него дополнительные заголовки или URL.
         *
         * Request request = chain.request();
         * request.newBuilder()
         *        .addHeader("sa", "as")
         *        .url("sad")
         *        .build();
         */

        // Выполняем исходный запрос и получаем ответ
        final Response response = chain.proceed(chain.request());

        // Проверяем, является ли ответ перенаправлением
        if (response.isRedirect()) {
            // Извлекаем заголовок "Location" из ответа
            String location = Objects.requireNonNull(response.header("Location"));

            // Проверяем, содержит ли заголовок "Location" параметр "code="
            if (location.contains("code=")) {
                // Извлекаем значение кода авторизации из строки заголовка
               final String authCode = StringUtils.substringAfter(location, "code=");

                // Сохраняем извлеченный код авторизации в расширении ApiLoginExtension
                ApiLoginExtension.setCode(authCode);
            }
        }
        return response;
    }
}