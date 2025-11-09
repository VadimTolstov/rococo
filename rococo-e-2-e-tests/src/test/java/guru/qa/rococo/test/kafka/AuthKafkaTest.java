package guru.qa.rococo.test.kafka;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.KafkaTest;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.AuthClient;
import guru.qa.rococo.service.api.AuthApiClient;
import guru.qa.rococo.service.kafka.KafkaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;

@KafkaTest
@DisplayName("Тесты kafka")
public class AuthKafkaTest {

  private final AuthClient authClient = new AuthApiClient();
  // private final UsersDbClient usersDbClient = new UsersDbClient();
  private static final String PASSWORD = Config.getInstance().defaultPassword();

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

//  @Test
//  @DisplayName("После отправки в kafka пользователь сохраняется в бд userdata")
//  void whenUserProducedToKafkaThenUserAddedToDb() throws Exception {
//    final String username = RandomDataUtils.randomUsername();
//
//    usersApiClient.createUser(username, PASSWORD);
//
//    assertNotNull(KafkaService.getUser(username));
//
//    UserJson userFromDb = usersDbClient.getUser(username);
//
//    Assertions.assertNotNull(userFromDb, "user not found in db: " + username);
//  }
}