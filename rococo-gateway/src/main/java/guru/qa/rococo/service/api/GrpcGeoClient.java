package guru.qa.rococo.service.api;

import guru.ga.rococo.grpc.*;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.page.RestPage;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;


@Component
public class GrpcGeoClient {

  private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoClient.class);

  @GrpcClient("grpcRococoClient")
  private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub rococoGeoServiceBlockingStub;

  public @Nonnull GeoJson getGeoById(@Nonnull UUID id) {
    try {
      GeoIdRequest request = GeoIdRequest.newBuilder()
          .setId(id.toString())
          .build();

      GeoResponse response = rococoGeoServiceBlockingStub.geo(request);
      return GeoJson.fromGrpcMessage(response);
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server for geo id: {} ", id, e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  public @Nonnull List<CountryJson> getCountries(int page, int size) {
    try {
      CountriesRequest request = CountriesRequest.newBuilder()
          .setPage(page)
          .setSize(size)
          .build();

      CountryListResponse response = rococoGeoServiceBlockingStub.countriesPage(request);
      return response.getCountriesList()
          .stream()
          .map(CountryJson::fromGrpcMessage)
          .toList();
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server for countries page: {}, size: {}", page, size, e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  public @Nonnull GeoJson addGeo(@Nonnull GeoJson geoJson) {
    try {
      GeoRequest request = GeoRequest.newBuilder()
          .setCity(geoJson.city())
          .setCountry(
              CountryResponse.newBuilder()
                  .setId(geoJson.country().id().toString())
                  .setName(geoJson.country().name())
                  .build()
          )
          .build();
      GeoResponse response = rococoGeoServiceBlockingStub.addGeo(request);
      return GeoJson.fromGrpcMessage(response);
    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server to add geo: {}", geoJson, e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }

  // Дополнительный метод для получения пагинированного ответа с метаданными
  public @Nonnull RestPage<CountryJson> getCountriesWithPagination(int page, int size) {
    try {
      final CountriesRequest request = CountriesRequest.newBuilder()
          .setPage(page)
          .setSize(size)
          .build();

      final CountryListResponse response = rococoGeoServiceBlockingStub.countriesPage(request);
      final List<CountryJson> content = response.getCountriesList()
          .stream()
          .map(CountryJson::fromGrpcMessage)
          .toList();

      return new RestPage<>(
          content,
          PageRequest.of(page, size),
          response.getTotalElements(),
          response.getTotalPages()
      );

    } catch (StatusRuntimeException e) {
      LOG.error("### Error while calling gRPC server for countries page: {}, size: {}", page, size, e);
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
    }
  }
}
