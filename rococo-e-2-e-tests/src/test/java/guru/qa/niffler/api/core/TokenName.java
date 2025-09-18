package guru.qa.niffler.api.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;

/**
 * Перечисление, представляющее различные типы токенов (куки) в системе.
 * Каждый элемент перечисления содержит имя куки.
 */
@Getter
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public enum TokenName {
    /**
     * Токен CSRF, используемый для защиты от межсайтовой подделки запросов (CSRF).
     */
    CSRF("XSRF-TOKEN"),
    /**
     * Сессионный токен JSESSIONID, используемый для идентификации сессии пользователя.
     */
    JSESSIONID("JSESSIONID");

    /**
     * Имя куки, связанное с токеном.
     */
    private final String cookieName;

    /**
     * Метод для получения элемента перечисления по имени куки.
     *
     * @param cookieName Имя куки, по которому нужно найти соответствующий токен.
     * @return Optional, содержащий элемент перечисления, если найден, иначе пустой Optional.
     */
    @NotNull
    public static Optional<TokenName> getEnumByCookieName(String cookieName) {
        return Arrays.stream(values()).filter(tokenName -> tokenName.getCookieName().equalsIgnoreCase(cookieName)).findFirst();
    }
}