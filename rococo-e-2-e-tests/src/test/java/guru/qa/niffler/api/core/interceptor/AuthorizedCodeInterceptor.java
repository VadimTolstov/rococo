package guru.qa.niffler.api.core.interceptor;


import guru.qa.niffler.api.core.store.AuthCodeStore;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;


/**
 * Перехватчик HTTP-запросов для автоматического извлечения authorization code из URL.
 * <p>
 * Этот интерцептор анализирует URL запросов на наличие параметра {@code code}, который
 * используется в OAuth 2.0 authorization code flow. При обнаружении кода он сохраняет
 * его в централизованное хранилище {@link AuthCodeStore} для последующего использования.
 * </p>
 *
 * <p>Основные функции:
 * <ul>
 *   <li>Сканирует query-параметры всех исходящих HTTP-запросов</li>
 *   <li>Обнаруживает параметр {@code code} в URL</li>
 *   <li>Сохраняет найденный код авторизации в thread-safe хранилище</li>
 *   <li>Прозрачно пропускает запросы дальше по цепочке</li>
 * </ul>
 *
 * <p>Типичное использование в OAuth flow:
 * <ol>
 *   <li>После аутентификации пользователя OAuth-сервер перенаправляет на redirect_uri с параметром code</li>
 *   <li>Интерцептор перехватывает запрос к redirect_uri до обработки клиентом</li>
 *   <li>Код извлекается и сохраняется для обмена на access token</li>
 * </ol>
 */
@ParametersAreNonnullByDefault
public class AuthorizedCodeInterceptor implements Interceptor {

    /**
     * Перехватывает HTTP-запрос для извлечения authorization code.
     * <p>
     * Анализирует URL запроса на наличие параметра {@code code}. Если параметр присутствует
     * и содержит значение, сохраняет это значение в {@link AuthCodeStore}.
     * </p>
     *
     * @param chain Цепочка перехватчиков OkHttp, содержит текущий запрос
     * @return Ответ от следующего обработчика в цепочке
     * @throws IOException При ошибках ввода/вывода во время обработки запроса
     */
    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        // Извлекаем значение параметра 'code' из URL запроса
        final String code = chain.request().url().queryParameter("code");

        // Если параметр 'code' присутствует и непустой
        if (StringUtils.isNotBlank(code)) {
            // Сохраняем код в хранилище для последующего использования
            AuthCodeStore.INSTANCE.setCode(code);
        }

        // Продолжаем обработку запроса следующими интерцепторами
        return chain.proceed(chain.request());
    }
}
