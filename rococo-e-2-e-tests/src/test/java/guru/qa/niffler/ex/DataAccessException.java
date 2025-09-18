package guru.qa.niffler.ex;

/**
 * Исключение для ошибок доступа к данным (БД, файлам и т.д.).
 * Наследуется от RuntimeException, чтобы не требовать обязательной обработки.
 */
public class DataAccessException extends RuntimeException {

    // Конструктор с сообщением об ошибке
    public DataAccessException(String message) {
        super(message);
    }

    // Конструктор с сообщением и причиной (вложенным исключением)
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    // Конструктор только с причиной
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}