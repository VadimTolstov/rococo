package guru.qa.rococo.ex;

public class GrpcNotFoundException extends RuntimeException {
  public GrpcNotFoundException(String message) {
    super(message);
  }
}
