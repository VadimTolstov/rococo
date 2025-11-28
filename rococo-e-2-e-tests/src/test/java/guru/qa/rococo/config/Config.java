package guru.qa.rococo.config;

import javax.annotation.Nonnull;
import java.util.List;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  @Nonnull
  default String projectId() {
    return "rococo-tolstov";
  }

  @Nonnull
  String frontUrl();

  @Nonnull
  String authUrl();

  @Nonnull
  String authJdbcUrl();

  @Nonnull
  String artistUrl();

  @Nonnull
  String artistJdbcUrl();

  @Nonnull
  String gatewayUrl();

  @Nonnull
  String userdataUrl();

  @Nonnull
  String userdataJdbcUrl();

  @Nonnull
  String museumUrl();

  @Nonnull
  String museumJdbcUrl();

  @Nonnull
  String paintingUrl();

  @Nonnull
  String paintingJdbcUrl();

  @Nonnull
  String dbPort();

  @Nonnull
  String dbDriver();

  default String ghUrl() {
    return "https://api.github.com/";
  }

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String imageContentBaseDir();

  @Nonnull
  String allureDockerServiceUrl();

  @Nonnull
  String kafkaAddress();

  @Nonnull
  default List<String> kafkaTopics() {
    return List.of("users");
  }

  @Nonnull
  default String defaultPassword() {
    return "12345";
  }

  default String url(String type, String host, String port) {
    return String.format("%s://%s:%s/", type, host, port);
  }
}
