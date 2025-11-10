package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  instance;

  @Nonnull
  private String localhost() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String dbDriver() {
    return "jdbc:postgresql";
  }

  @NotNull
  @Override
  public String dbPort() {
    return "5432";
  }

  @NotNull
  @Override
  public String frontUrl() {
    return serverUrl("3000");
  }

  @NotNull
  @Override
  public String authUrl() {
    return serverUrl("9000");
  }

  @NotNull
  @Override
  public String authJdbcUrl() {
    return dbUrl() + "rococo-auth";
  }

  @NotNull
  @Override
  public String gatewayUrl() {
    return serverUrl("8090");
  }

  @NotNull
  @Override
  public String userdataUrl() {
    return serverUrl("8285");
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return dbUrl() + "rococo-userdata";
  }

  @NotNull
  @Override
  public String artistUrl() {
    return serverUrl("8282");
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return dbUrl() + "rococo-artist";
  }

  @NotNull
  @Override
  public String museumUrl() {
    return serverUrl("8283");
  }

  @NotNull
  @Override
  public String museumJdbcUrl() {
    return dbUrl() + "rococo-museum";
  }

  @NotNull
  @Override
  public String paintingUrl() {
    return serverUrl("8284");
  }

  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return dbUrl() + "rococo-painting";
  }

  @NotNull
  @Override
  public String allureDockerServiceUrl() {
    return serverUrl("5050");
  }

  @Nonnull
  @Override
  public String kafkaAddress() {
    return "127.0.0.1:9092";
  }

  @NotNull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }

  @NotNull
  @Override
  public String imageContentBaseDir() {
    return "img/content/";
  }


  @Nonnull
  private String dbUrl() {
    return url(dbDriver(), localhost(), dbPort());
  }

  @Nonnull
  private String serverUrl(@Nonnull String port) {
    return url("http", localhost(), port);
  }
}