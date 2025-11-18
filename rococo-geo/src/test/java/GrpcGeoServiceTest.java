import guru.ga.rococo.grpc.*;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.ex.GrpcBadRequestException;
import guru.qa.rococo.ex.GrpcNotFoundException;
import guru.qa.rococo.service.GrpcGeoService;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcGeoServiceTest {

  private GrpcGeoService grpcGeoService;

  @Mock
  private CountryRepository countryRepository;

  @Mock
  private GeoRepository geoRepository;

  @Mock
  private StreamObserver<CountryListResponse> countryListResponseObserver;

  @Mock
  private StreamObserver<GeoResponse> geoResponseObserver;

  private CountryEntity testCountry;
  private GeoEntity testGeo;
  private UUID testCountryId;
  private UUID testGeoId;

  @BeforeEach
  void setUp() {
    grpcGeoService = new GrpcGeoService(countryRepository, geoRepository);

    testCountryId = UUID.randomUUID();
    testGeoId = UUID.randomUUID();

    testCountry = new CountryEntity();
    testCountry.setId(testCountryId);
    testCountry.setName("Test Country");

    testGeo = new GeoEntity();
    testGeo.setId(testGeoId);
    testGeo.setCity("Test City");
    testGeo.setCountry(testCountry);
  }

  @Test
  void countriesPageShouldReturnPaginatedCountries() {
    // Given
    CountriesRequest request = CountriesRequest.newBuilder()
        .setPage(0)
        .setSize(10)
        .build();

    List<CountryEntity> countries = List.of(testCountry);
    Page<CountryEntity> page = new PageImpl<>(
        countries,
        PageRequest.of(0, 10),
        countries.size()
    );

    when(countryRepository.findAll(any(Pageable.class))).thenReturn(page);

    // When
    grpcGeoService.countriesPage(request, countryListResponseObserver);

    // Then
    ArgumentCaptor<CountryListResponse> responseCaptor = ArgumentCaptor.forClass(CountryListResponse.class);
    verify(countryListResponseObserver).onNext(responseCaptor.capture());
    verify(countryListResponseObserver).onCompleted();

    CountryListResponse response = responseCaptor.getValue();
    assertEquals(1, response.getCountriesCount());
    assertEquals("Test Country", response.getCountries(0).getName());
    assertEquals(testCountryId.toString(), response.getCountries(0).getId());
    assertEquals(10, response.getSize());
    assertEquals(0, response.getNumber());
    assertEquals(1, response.getTotalPages());
    assertEquals(1, response.getTotalElements());
  }

  @Test
  void countriesPageShouldHandleRepositoryException() {
    // Given
    CountriesRequest request = CountriesRequest.newBuilder()
        .setPage(0)
        .setSize(10)
        .build();

    when(countryRepository.findAll(any(Pageable.class)))
        .thenThrow(new RuntimeException("Database error"));

    // When
    grpcGeoService.countriesPage(request, countryListResponseObserver);

    // Then
    ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
    verify(countryListResponseObserver).onError(throwableCaptor.capture());

    Throwable throwable = throwableCaptor.getValue();
    assertInstanceOf(StatusRuntimeException.class, throwable);
    assertTrue(throwable.getMessage().contains("Error fetching countries"));
  }

  @Test
  void geoShouldReturnGeoWhenFound() {
    // Given
    GeoIdRequest request = GeoIdRequest.newBuilder()
        .setId(testGeoId.toString())
        .build();

    when(geoRepository.findById(testGeoId)).thenReturn(Optional.of(testGeo));
    when(countryRepository.findById(testCountryId)).thenReturn(Optional.of(testCountry));

    // When
    grpcGeoService.geo(request, geoResponseObserver);

    // Then
    ArgumentCaptor<GeoResponse> responseCaptor = ArgumentCaptor.forClass(GeoResponse.class);
    verify(geoResponseObserver).onNext(responseCaptor.capture());
    verify(geoResponseObserver).onCompleted();

    GeoResponse response = responseCaptor.getValue();
    assertEquals(testGeoId.toString(), response.getId());
    assertEquals("Test City", response.getCity());
    assertEquals(testCountryId.toString(), response.getCountry().getId());
    assertEquals("Test Country", response.getCountry().getName());
  }

  @Test
  void geoShouldThrowExceptionWhenIdIsEmpty() {
    // Given
    GeoIdRequest request = GeoIdRequest.newBuilder()
        .setId("")
        .build();

    // When & Then
    GrpcBadRequestException exception = assertThrows(
        GrpcBadRequestException.class,
        () -> grpcGeoService.geo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("GeoIdRequest передано c некорректным id"));
    verifyNoInteractions(geoRepository);
    verifyNoInteractions(countryRepository);
  }

  @Test
  void geoShouldThrowExceptionWhenGeoNotFound() {
    // Given
    GeoIdRequest request = GeoIdRequest.newBuilder()
        .setId(testGeoId.toString())
        .build();

    when(geoRepository.findById(testGeoId)).thenReturn(Optional.empty());

    // When & Then
    GrpcNotFoundException exception = assertThrows(
        GrpcNotFoundException.class,
        () -> grpcGeoService.geo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("Геолокация не найдена"));
    verify(geoRepository).findById(testGeoId);
    verifyNoInteractions(countryRepository);
  }

  @Test
  void geoShouldThrowExceptionWhenCountryNotFound() {
    // Given
    GeoIdRequest request = GeoIdRequest.newBuilder()
        .setId(testGeoId.toString())
        .build();

    when(geoRepository.findById(testGeoId)).thenReturn(Optional.of(testGeo));
    when(countryRepository.findById(testCountryId)).thenReturn(Optional.empty());

    // When & Then
    GrpcNotFoundException exception = assertThrows(
        GrpcNotFoundException.class,
        () -> grpcGeoService.geo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("Страна не найдена"));
    verify(geoRepository).findById(testGeoId);
    verify(countryRepository).findById(testCountryId);
  }

  @Test
  void addGeoShouldCreateNewGeo() {
    // Given
    GeoRequest request = GeoRequest.newBuilder()
        .setCity("New City")
        .setCountry(CountryResponse.newBuilder()
            .setId(testCountryId.toString())
            .setName("Test Country")
            .build())
        .build();

    when(countryRepository.findById(testCountryId)).thenReturn(Optional.of(testCountry));

    GeoEntity savedGeo = new GeoEntity(testGeoId, "New City", testCountry);
    when(geoRepository.save(any(GeoEntity.class))).thenReturn(savedGeo);

    // When
    grpcGeoService.addGeo(request, geoResponseObserver);

    // Then
    ArgumentCaptor<GeoEntity> geoCaptor = ArgumentCaptor.forClass(GeoEntity.class);
    verify(geoRepository).save(geoCaptor.capture());

    GeoEntity capturedGeo = geoCaptor.getValue();
    assertNull(capturedGeo.getId()); // ID должен быть null для новой сущности
    assertEquals("New City", capturedGeo.getCity());
    assertEquals(testCountry, capturedGeo.getCountry());

    ArgumentCaptor<GeoResponse> responseCaptor = ArgumentCaptor.forClass(GeoResponse.class);
    verify(geoResponseObserver).onNext(responseCaptor.capture());
    verify(geoResponseObserver).onCompleted();

    GeoResponse response = responseCaptor.getValue();
    assertEquals(testGeoId.toString(), response.getId());
    assertEquals("New City", response.getCity());
    assertEquals(testCountryId.toString(), response.getCountry().getId());
    assertEquals("Test Country", response.getCountry().getName());
  }

  @Test
  void addGeoShouldThrowExceptionWhenCityIsEmpty() {
    // Given
    GeoRequest request = GeoRequest.newBuilder()
        .setCity("")
        .setCountry(CountryResponse.newBuilder()
            .setId(testCountryId.toString())
            .setName("Test Country")
            .build())
        .build();

    // When & Then
    GrpcBadRequestException exception = assertThrows(
        GrpcBadRequestException.class,
        () -> grpcGeoService.addGeo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("GeoRequest передано с дефолтными значениями"));
    verifyNoInteractions(countryRepository);
    verifyNoInteractions(geoRepository);
  }

  @Test
  void addGeoShouldThrowExceptionWhenCountryIdIsEmpty() {
    // Given
    GeoRequest request = GeoRequest.newBuilder()
        .setCity("New City")
        .setCountry(CountryResponse.newBuilder()
            .setId("")
            .setName("Test Country")
            .build())
        .build();

    // When & Then
    GrpcBadRequestException exception = assertThrows(
        GrpcBadRequestException.class,
        () -> grpcGeoService.addGeo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("GeoRequest передано с дефолтными значениями"));
    verifyNoInteractions(countryRepository);
    verifyNoInteractions(geoRepository);
  }

  @Test
  void addGeoShouldThrowExceptionWhenCountryNotFound() {
    // Given
    GeoRequest request = GeoRequest.newBuilder()
        .setCity("New City")
        .setCountry(CountryResponse.newBuilder()
            .setId(testCountryId.toString())
            .setName("Test Country")
            .build())
        .build();

    when(countryRepository.findById(testCountryId)).thenReturn(Optional.empty());

    // When & Then
    GrpcNotFoundException exception = assertThrows(
        GrpcNotFoundException.class,
        () -> grpcGeoService.addGeo(request, geoResponseObserver)
    );

    assertTrue(exception.getMessage().contains("Страна не найдена"));
    verify(countryRepository).findById(testCountryId);
    verifyNoInteractions(geoRepository);
  }

  static Stream<Arguments> countriesPageShouldHandleDifferentPageSizes() {
    return Stream.of(
        Arguments.of(0, 10, 5, 1),   // page 0, size 10, total 5, expected pages 1
        Arguments.of(0, 5, 15, 3),    // page 0, size 5, total 15, expected pages 3
        Arguments.of(1, 3, 10, 4),    // page 1, size 3, total 10, expected pages 4
        Arguments.of(0, 20, 0, 0)     // page 0, size 20, total 0, expected pages 0
    );
  }

  @MethodSource
  @ParameterizedTest
  void countriesPageShouldHandleDifferentPageSizes(int page, int size, int totalElements, int expectedTotalPages) {
    // Given
    CountriesRequest request = CountriesRequest.newBuilder()
        .setPage(page)
        .setSize(size)
        .build();

    int elementsOnThisPage = calculateElementsOnPage(page, size, totalElements);
    List<CountryEntity> countries = createCountriesList(elementsOnThisPage);

    Page<CountryEntity> mockPage = new PageImpl<>(
        countries,
        PageRequest.of(page, size),
        totalElements
    );

    when(countryRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

    // When
    grpcGeoService.countriesPage(request, countryListResponseObserver);

    // Then
    ArgumentCaptor<CountryListResponse> responseCaptor = ArgumentCaptor.forClass(CountryListResponse.class);
    verify(countryListResponseObserver).onNext(responseCaptor.capture());
    verify(countryListResponseObserver).onCompleted();

    CountryListResponse response = responseCaptor.getValue();

    // Правильные assertions:
    assertEquals(size, response.getSize());
    assertEquals(page, response.getNumber());
    assertEquals(expectedTotalPages, response.getTotalPages());
    assertEquals(totalElements, response.getTotalElements());
    assertEquals(elementsOnThisPage, response.getCountriesCount());
  }

  private int calculateElementsOnPage(int page, int size, int totalElements) {
    if (totalElements == 0) return 0;

    int start = page * size;
    if (start >= totalElements) return 0;

    return Math.min(size, totalElements - start);
  }

  private List<CountryEntity> createCountriesList(int count) {
    if (count == 0) return List.of();

    return java.util.stream.IntStream.range(0, count)
        .mapToObj(i -> {
          CountryEntity country = new CountryEntity();
          country.setId(UUID.randomUUID());
          country.setName("Country " + i);
          return country;
        })
        .collect(java.util.stream.Collectors.toList());
  }

  // Параметризованные тесты для различных невалидных UUID
  static Stream<Arguments> geoShouldHandleInvalidUuidFormats() {
    return Stream.of(
        Arguments.of("invalid-uuid"),
        Arguments.of("123"),
        Arguments.of(" "),
        Arguments.of("null")
    );
  }

  @MethodSource
  @ParameterizedTest
  void geoShouldHandleInvalidUuidFormats(String invalidUuid) {
    // Given
    GeoIdRequest request = GeoIdRequest.newBuilder()
        .setId(invalidUuid)
        .build();

    // When & Then
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> grpcGeoService.geo(request, geoResponseObserver)
    );
  }
}