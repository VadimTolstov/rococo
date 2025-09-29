package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.userdata.UserJson;
import org.junit.jupiter.api.Test;

public class MainTest {


  @ApiLogin(password = "12345",username = "books2")
  @Test
  public void firstTest(UserJson userJson) {
    System.out.println(userJson);
    System.out.println("ПАРОЛЬ !!!!!!!!!!! = " + userJson.password());
    Selenide.open("http://127.0.0.1:3000/");
    Selenide.sleep(3000);
  }
}
