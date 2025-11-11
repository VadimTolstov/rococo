package guru.qa.rococo.test.fake;

import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.db.ArtistDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestTest
public class ArtistDbTest {
  private static final String IMAGE_DIR = "artists";
  private final ArtistDbClient artistDbClient = new ArtistDbClient();

  @RegisterExtension
  private static final ApiLoginExtension extension = ApiLoginExtension.rest();

  @Test
  @Content(artistCount = 2)
  void test(ContentJson contentJson) {
//    List<ArtistJson> list =new ArrayList<>(contentJson.artists());
//  //  final ArtistJson request = contentJson.artists().iterator().next();
//    System.out.println(list);

//
//    final ArtistJson request = new ArtistJson(
//        UUID.fromString("9e16c5eb-92cd-4273-9464-a700860b00ae"),
//        RandomDataUtils.randomArtistName(),
//        RandomDataUtils.shortBio(),
//        RandomDataUtils.randomImageString(IMAGE_DIR)
////    );
    List<UUID> list = new ArrayList<>();
//    list.add(UUID.fromString("976dd89e-b94b-11f0-8a0f-625582b62986"));
    list.add(UUID.fromString("fde661ec-3cde-4a85-9068-402002c12203"));
    artistDbClient.removeList(list);
  }
}
