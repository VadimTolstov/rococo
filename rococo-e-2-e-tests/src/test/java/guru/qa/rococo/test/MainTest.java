package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

public class MainTest {


 // @ApiLogin(password = "12345",username = "test")
  @Test
  public void firstTest() {
    PaintingJson painting = new PaintingJson(
        null, // id
        "Female nude", // title
        "Картина «Обнаженная» была написана Пьером Ренуаром в 1876 году. Это одна из многочисленных работ художника, изображающих его видение истинной женской красоты. Полотно выполнено по всем правилам импрессионизма.", // description
        null, // content (base64 изображения)
        null, // artist
        null  // museum
    );
    Selenide.open(MainPage.URL, MainPage.class)
        .clickPaintingsLink()
        .openDetailPage("Female nude")
        .hasPainting(painting);
  }
}
