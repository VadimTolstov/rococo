package guru.qa.rococo.utils;

import com.github.javafaker.Faker;
import guru.qa.rococo.config.Config;
import net.javacrumbs.jsonunit.core.util.ResourceUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static guru.qa.rococo.utils.PhotoConverter.loadImageAsBytes;

@ParametersAreNonnullByDefault
public class RandomDataUtils {
  private static final Faker faker = new Faker();

  public static @Nonnull String randomUsername() {
    return faker.name().username() + " " + faker.name().firstName();
  }

  public static @Nonnull String randomArtistName() {
    return faker.artist().name() + " " + faker.name().firstName();
  }

  public static @Nonnull String randomSurname() {
    return faker.name().lastName();
  }

  public static @Nonnull String shortBio() {
    return faker.lorem().paragraph();
  }

  public static @Nonnull String city() {
    return faker.address().city();
  }

  public static @Nonnull String museum() {
    return faker.university().name() + " " + city() + " Museum";
  }

  public static @Nonnull String painting() {
    return faker.book().title() + " " + faker.weather().description();
  }

  public static @Nonnull String randomSentence(int wordsCount) {
    if (wordsCount <= 0) {
      throw new IllegalArgumentException("Words count must be greater than zero");
    }
    return faker.lorem().sentence(wordsCount).trim();
  }

  public static @Nonnull String randomPassword() {
    return faker.internet().password(3, 10);
  }

  public static byte[] randomImage(String folderName) {
    return loadImageAsBytes(randomFilePath(folderName));
  }

  public static String randomImageString(String folderName) {
    return PhotoConverter.loadImageAsString(randomFilePath(folderName));
  }

  public static String randomFilePath(String folderName) {
    folderName = Config.getInstance().imageContentBaseDir() + folderName;
    URL resource = ResourceUtils.class.getClassLoader().getResource(folderName);
    if (resource == null) {
      throw new IllegalArgumentException("Folder not found: " + folderName);
    }

    File folder;
    try {
      folder = new File(resource.toURI());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    if (!folder.isDirectory()) {
      throw new IllegalArgumentException(folderName + " is not a directory");
    }

    File[] files = folder.listFiles();
    if (files == null || files.length == 0) {
      throw new IllegalStateException("No files in folder: " + folderName);
    }

    File randomFile = files[ThreadLocalRandom.current().nextInt(files.length)];
    return folderName + "/" + randomFile.getName();
  }

  public static String fakeJwt() {
    String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d}",
        UUID.randomUUID(), System.currentTimeMillis() / 1000);

    String header = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(headerJson.getBytes());
    String payload = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(payloadJson.getBytes());

    String signature = Base64.getUrlEncoder().withoutPadding()
        .encodeToString(UUID.randomUUID().toString().getBytes());

    return header + "." + payload + "." + signature;
  }
}
