package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

enum LocalConfig implements Config {
    instance;

    @NotNull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @NotNull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @NotNull
    @Override
    public String authJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-auth";
    }

    @NotNull
    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:8090/";
    }

    @NotNull
    @Override
    public String userdataUrl() {
        return "http://127.0.0.1:8285/";
    }

    @NotNull
    @Override
    public String userdataJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-userdata";
    }

    @NotNull
    @Override
    public String artistUrl() {
        return "http://127.0.0.1:8282/";
    }

    @NotNull
    @Override
    public String artistJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-artist";
    }

    @NotNull
    @Override
    public String museumUrl() {
        return "http://127.0.0.1:8283/";
    }

    @NotNull
    @Override
    public String museumJdbcUrl() {
        return "jdbc:postgresql://127.0.0.1:5432/rococo-museum";
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
    public String screenshotBaseDir() {
        return "screenshots/local/";
    }

    @NotNull
    @Override
    public String allureDockerServiceUrl() {
        return "http://127.0.0.1:5050/";
    }
}