package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.ga.rococo.grpc.CountryResponse;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CountryJson(
    @NotNull(message = "country.id: ID страны обязателен для заполнения")
    @JsonProperty("id")
    UUID id,

    @JsonProperty("name")
    String name
) {
  public static CountryJson fromGrpcMessage(@Nonnull CountryResponse response) {
    return new CountryJson(
        UUID.fromString(response.getId()),
        response.getName()
    );
  }
}