package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.instance
                : LocalConfig.instance;
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


    default String ghUrl() {
        return "https://api.github.com/";
    }

    @Nonnull
    String screenshotBaseDir();

    @Nonnull
    String allureDockerServiceUrl();
}
