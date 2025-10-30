package guru.qa.rococo.test;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.Country;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.service.api.PaintingApiClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@WebTest
public class MainTest {

  @Content(
      paintingCount = 10
  )

  @Test
  void test123() {
    PaintingJson page = new PaintingApiClient().createPainting(  new PaintingJson(
        null, // id
        "Female nude", // title
        "Картина «Обнаженная» была написана Пьером Ренуаром в 1876 году. Это одна из многочисленных работ художника, изображающих его видение истинной женской красоты. Полотно выполнено по всем правилам импрессионизма.", // description
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==", // content (base64 изображения)
        new ArtistJson(UUID.fromString("01cfd421-e749-4215-8767-71f69e069226"),"dsf","dsf", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="), // artist
        new MuseumJson(UUID.fromString("a7fa39cc-006c-4aea-a959-7e8b30f5a84b"),"sad","sadf","data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==", new GeoJson("sdad", new CountryJson(UUID.randomUUID(),Country.random().getCountry())))  // museum
    ));
    System.out.println("!!!!!!!!!!!!!!!!!!!" + page);

  }

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
        .checkDetailsArtist(artist);
  }
@User
  @ApiLogin()
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
