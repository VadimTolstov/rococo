package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;

import java.util.Date;

/**
 * Модель данных, представляющая сессию пользователя.
 * <p>
 * Содержит информацию о:
 * <ul>
 *   <li>Имени пользователя</li>
 *   <li>Времени создания сессии</li>
 *   <li>Времени истечения сессии</li>
 * </ul>
 * Record является иммутабельным (неизменяемым) объектом.
 */
public record SessionJson(
        @JsonProperty("username")
        String username,
        @JsonProperty("issuedAt")
        Date issuedAt,
        @JsonProperty("expiresAt")
        Date expiresAt) {

    /**
     * Создает и возвращает пустой объект сессии.
     * <p>
     * Используется в случаях, когда необходимо представить отсутствующую или невалидную сессию.
     * Все поля возвращаемого объекта будут иметь значение {@code null}.
     *
     * @return Пустой объект сессии с null-значениями во всех полях
     */
    public static @Nonnull
    SessionJson empty() {
        return new SessionJson(null, null, null);
    }
}