package service;

import guru.ga.rococo.grpc.*;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.service.api.GrpcGeoClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcGeoClientTest {

  private GrpcGeoClient grpcGeoClient;

  @Mock
  private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub rococoGeoServiceBlockingStub;

  private UUID testGeoId;
  private UUID testCountryId;
  private GeoResponse testGeoResponse;
  private CountryResponse testCountryResponse;
  private CountryListResponse testCountryListResponse;

  @BeforeEach
  void setUp() {
    grpcGeoClient = new GrpcGeoClient();

    // Используем рефлексию для установки мока, так как поле приватное
    try {
      var field = GrpcGeoClient.class.getDeclaredField("rococoGeoServiceBlockingStub");
      field.setAccessible(true);
      field.set(grpcGeoClient, rococoGeoServiceBlockingStub);
    } catch (Exception e) {
      throw new RuntimeException("Failed to set mock via reflection", e);
    }

    testGeoId = UUID.randomUUID();
    testCountryId = UUID.randomUUID();

    testCountryResponse = CountryResponse.newBuilder()
        .setId(testCountryId.toString())
        .setName("Test Country")
        .build();

    testGeoResponse = GeoResponse.newBuilder()
        .setId(testGeoId.toString())
        .setCity("Test City")
        .setCountry(testCountryResponse)
        .build();

    testCountryListResponse = CountryListResponse.newBuilder()
        .setSize(10)
        .setNumber(0)
        .setTotalPages(1)
        .setTotalElements(1)
        .addCountries(testCountryResponse)
        .build();
  }

  @Test
  void getGeoByIdShouldReturnGeoWhenFound() {
    // Given
    when(rococoGeoServiceBlockingStub.geo(any(GeoIdRequest.class)))
        .thenReturn(testGeoResponse);

    // When
    GeoJson result = grpcGeoClient.getGeoById(testGeoId);

    // Then
    assertNotNull(result);
    assertEquals("Test City", result.city());
    assertEquals(testCountryId, result.country().id());
    assertEquals("Test Country", result.country().name());

    verify(rococoGeoServiceBlockingStub).geo(argThat(request ->
        request.getId().equals(testGeoId.toString())
    ));
  }

  @Test
  void getGeoByIdShouldThrowExceptionWhenGrpcFails() {
    // Given
    when(rococoGeoServiceBlockingStub.geo(any(GeoIdRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> grpcGeoClient.getGeoById(testGeoId)
    );

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertTrue(exception.getMessage().contains("The gRPC operation was cancelled"));

    verify(rococoGeoServiceBlockingStub).geo(any(GeoIdRequest.class));
  }

  @Test
  void getCountriesShouldReturnListOfCountries() {
    // Given
    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenReturn(testCountryListResponse);

    // When
    List<CountryJson> result = grpcGeoClient.getCountries(0, 10);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());

    CountryJson country = result.get(0);
    assertEquals(testCountryId, country.id());
    assertEquals("Test Country", country.name());

    verify(rococoGeoServiceBlockingStub).countriesPage(argThat(request ->
        request.getPage() == 0 && request.getSize() == 10
    ));
  }

  @Test
  void getCountriesShouldReturnEmptyListWhenNoCountries() {
    // Given
    CountryListResponse emptyResponse = CountryListResponse.newBuilder()
        .setSize(10)
        .setNumber(0)
        .setTotalPages(0)
        .setTotalElements(0)
        .build();

    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenReturn(emptyResponse);

    // When
    List<CountryJson> result = grpcGeoClient.getCountries(0, 10);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void getCountriesShouldThrowExceptionWhenGrpcFails() {
    // Given
    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> grpcGeoClient.getCountries(0, 10)
    );

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertTrue(exception.getMessage().contains("The gRPC operation was cancelled"));
  }

  @Test
  void addGeoShouldReturnCreatedGeo() {
    // Given
    GeoJson newGeo = new GeoJson(
        "New City",
        new CountryJson(testCountryId, "Test Country")
    );

    when(rococoGeoServiceBlockingStub.addGeo(any(GeoRequest.class)))
        .thenReturn(testGeoResponse);

    // When
    GeoJson result = grpcGeoClient.addGeo(newGeo);

    // Then
    assertNotNull(result);
    assertEquals("Test City", result.city());
    assertEquals(testCountryId, result.country().id());

    verify(rococoGeoServiceBlockingStub).addGeo(argThat(request ->
        request.getCity().equals("New City") &&
            request.getCountry().getId().equals(testCountryId.toString())
    ));
  }

  @Test
  void addGeoShouldThrowExceptionWhenGrpcFails() {
    // Given
    GeoJson newGeo = new GeoJson(
        "New City",
        new CountryJson(testCountryId, "Test Country")
    );

    when(rococoGeoServiceBlockingStub.addGeo(any(GeoRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> grpcGeoClient.addGeo(newGeo)
    );

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertTrue(exception.getMessage().contains("The gRPC operation was cancelled"));
  }

  @Test
  void getCountriesPageShouldReturnFullResponse() {
    // Given
    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenReturn(testCountryListResponse);

    // When
    CountryListResponse result = grpcGeoClient.getCountriesPage(1, 5);

    // Then
    assertNotNull(result);
    assertEquals(10, result.getSize());
    assertEquals(0, result.getNumber());
    assertEquals(1, result.getTotalPages());
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getCountriesCount());

    verify(rococoGeoServiceBlockingStub).countriesPage(argThat(request ->
        request.getPage() == 1 && request.getSize() == 5
    ));
  }

  @Test
  void getCountriesPageShouldThrowExceptionWhenGrpcFails() {
    // Given
    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenThrow(new StatusRuntimeException(Status.INTERNAL));

    // When & Then
    ResponseStatusException exception = assertThrows(
        ResponseStatusException.class,
        () -> grpcGeoClient.getCountriesPage(0, 10)
    );

    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatusCode());
    assertTrue(exception.getMessage().contains("The gRPC operation was cancelled"));
  }

  @Test
  void getGeoByIdShouldHandleNullId() {
    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> grpcGeoClient.getGeoById(null)
    );
  }

  @Test
  void addGeoShouldHandleNullGeo() {
    // When & Then
    assertThrows(
        NullPointerException.class,
        () -> grpcGeoClient.addGeo(null)
    );
  }

  @Test
  void getCountriesShouldHandleNegativePageAndSize() {
    // Given
    when(rococoGeoServiceBlockingStub.countriesPage(any(CountriesRequest.class)))
        .thenReturn(testCountryListResponse);

    // When
    List<CountryJson> result = grpcGeoClient.getCountries(-1, -5);

    // Then
    assertNotNull(result);
    verify(rococoGeoServiceBlockingStub).countriesPage(argThat(request ->
        request.getPage() == -1 && request.getSize() == -5
    ));
  }
}