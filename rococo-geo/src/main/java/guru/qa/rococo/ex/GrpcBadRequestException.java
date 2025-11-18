package guru.qa.rococo.ex;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GrpcBadRequestException extends RuntimeException {
  public GrpcBadRequestException(String massage) {
    super(massage);
  }
}
