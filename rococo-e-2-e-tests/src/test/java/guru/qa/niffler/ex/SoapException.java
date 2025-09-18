package guru.qa.niffler.ex;

public class SoapException extends RuntimeException {
    public SoapException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapException(String message) {
        super(message);
    }
}
