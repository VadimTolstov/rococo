package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Objects;

enum DockerConfig implements Config {
    instance;

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

    @Nonnull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @Nonnull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @Nonnull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-auth";
    }

    @Nonnull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8090/";
    }

    @Nonnull
    @Override
    public String userdataUrl() {
        return "http://userdata.rococo.dc:8285/";
    }

    @Nonnull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-userdata";
    }

    @Nonnull
    @Override
    public String artistUrl() {
        return "http://spend.rococo.dc:8282/";
    }

    @Nonnull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-artist";
    }

    @Nonnull
    @Override
    public String museumUrl() {
        return "http://spend.rococo.dc:8283/";
    }

    @Nonnull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:postgresql://rococo-all-db:5432/rococo-museum";
    }

    @NotNull
    @Override
    public String paintingUrl() {
        return "http://127.0.0.1:8284/";
    }

    @NotNull
    @Override
    public String paintingJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-painting";
    }


    @NotNull
    @Override
    public String allureDockerServiceUrl() {
        String allureDockerApiUrl = System.getenv("ALLURE_DOCKER_API");
        return Objects.requireNonNullElse(allureDockerApiUrl, "http://allure:5050/");
    }

    @NotNull
    @Override
    public String screenshotBaseDir() {
        return "screenshots/selenoid/";
    }

    @NotNull
    @Override
    public String imageContentBaseDir() {
        return "img/content/";
    }

    @Nonnull
    @Override
    public String kafkaAddress() {
        return "kafka:9092";
    }
}