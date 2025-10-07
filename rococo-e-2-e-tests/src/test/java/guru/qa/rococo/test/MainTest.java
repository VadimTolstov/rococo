package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

public class MainTest {


  @ApiLogin(password = "12345", username = "test")
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
        .checkDetailPainting(painting);
  }

  @ApiLogin(password = "12345", username = "test")
  @Test
  public void artistTest() {
    ArtistJson artist = new ArtistJson(
        null,
        "Ренуар",
        "Французский живописец, график и скульптор, один из основных представителей импрессионизма.",
        null
    );
    Selenide.open(MainPage.URL, MainPage.class)
        .clickArtistsLink()
        .openDetailPage(artist.name())
        .checkDetailPainting(artist);
  }

  @ApiLogin(password = "12345", username = "test")
  @Test
  public void museumTest() {
    MuseumJson museumJson = new MuseumJson(
        null,
        "Третьяковка",
        "Государственная Третьяковская галерея — российский государственный художественный музей в Москве, созданный на основе исторических коллекций купцов братьев Павла и Сергея Михайловичей Третьяковых; одно из крупнейших в мире собраний русского изобразительного искусства.",
        null,
        new GeoJson("Москва", new CountryJson(null, "Россия"))
    );
    Selenide.open(MainPage.URL, MainPage.class)
        .clickMuseumsLink()
        .openDetailPage(museumJson.title())
        .checkDetailMuseum(museumJson);
  }
}
