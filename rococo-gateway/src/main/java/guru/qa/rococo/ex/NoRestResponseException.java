package guru.qa.rococo.ex;

/**
 * Исключение, выбрасываемое при отсутствии ответа от REST-сервиса.
 * <p>
 * Может возникать в следующих сценариях:
 * <ul>
 *   <li>Сервис недоступен (connection refused)</li>
 *   <li>Таймаут соединения</li>
 *   <li>Ошибка десериализации ответа</li>
 * </ul>
 */
public class NoRestResponseException extends RuntimeException {

    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message Детализированное описание ошибки.
     *                Рекомендуемый формат: "Ошибка при вызове [СЕРВИС]: [ОПИСАНИЕ]".
     *                Пример: "Ошибка при вызове UserService: Timeout 5000ms exceeded"
     */
    public NoRestResponseException(String message) {
        super(message);
    }
}