package guru.qa.niffler.data.dao.impl.springJdbc.utils;

import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DaoUtils {

    public static @Nonnull UUID getGeneratedId(@Nonnull KeyHolder kh, @Nonnull String keyName) {
        // Проверяем, что KeyHolder и его ключи не null
        if (kh.getKeys() == null) {
            throw new DataAccessException("Ключи не были сгенерированы");
        }

        // Учитываем регистр ключа (например, "ID" вместо "id")
        String idKey = kh.getKeys().keySet().stream()
                .filter(key -> key.equalsIgnoreCase(keyName))
                .findFirst()
                .orElseThrow(() -> new DataAccessException("Ключ " + keyName + " не найден"));

        // Получаем значение ключа
        Object idValue = kh.getKeys().get(idKey);

        // Преобразуем строку в UUID (если БД возвращает UUID как строку)
        if (idValue instanceof String) {
            try {
                return UUID.fromString((String) idValue);
            } catch (IllegalArgumentException e) {
                throw new DataAccessException("Некорректный формат UUID: " + idValue, e);
            }
        }

        // Проверяем, что значение является UUID
        if (!(idValue instanceof UUID)) {
            throw new DataAccessException("ID должен быть типа UUID. Получен тип: " + idValue.getClass());
        }

        return (UUID) idValue;
    }
}
