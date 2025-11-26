package guru.qa.rococo.test.kafka;

import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.KafkaTest;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.kafka.KafkaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@KafkaTest
@DisplayName("Тесты kafka")
public class AuthKafkaTest {

  @Test
  @User
  @DisplayName("При регистрации пользователь отправляется в kafka")
  void userShouldBeProducedToKafka(UserJson user) throws Exception {
    final String username = user.username();

    final UserJson userFromKafka = Objects.requireNonNull(KafkaService.getUser(username));
    Assertions.assertEquals(
        username,
        userFromKafka.username()
    );
  }
}