package guru.qa.rococo.service;

import guru.qa.grpc.rococo.*;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.GeoEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.ex.GrpcBadRequestException;
import guru.qa.rococo.ex.GrpcNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@GrpcService
public class GrpcGeoService extends RococoGeoServiceGrpc.RococoGeoServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(GrpcGeoService.class);

  private final CountryRepository countryRepository;
  private final GeoRepository geoRepository;

  @Autowired
  public GrpcGeoService(CountryRepository countryRepository, GeoRepository geoRepository) {
    this.countryRepository = countryRepository;
    this.geoRepository = geoRepository;
  }

  @Transactional(readOnly = true)
  @Override
  public void countriesPage(CountriesRequest request, StreamObserver<CountryListResponse> responseObserver) {
    try {
      final Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
      final Page<CountryEntity> countries = countryRepository.findAll(pageable);

      CountryListResponse.Builder responseBuilder = CountryListResponse.newBuilder();

      for (CountryEntity country : countries.getContent()) {
        responseBuilder.addCountries(
            CountryResponse.newBuilder()
                .setId(country.getId().toString())
                .setName(country.getName())
                .build()
        );
      }

      // Добавляем метаданные пагинации в ответ
      responseBuilder
          .setSize(countries.getSize())
          .setNumber(countries.getNumber())
          .setTotalPages(countries.getTotalPages())
          .setTotalElements((int) countries.getTotalElements());

      responseObserver.onNext(responseBuilder.build());
      responseObserver.onCompleted();
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL
          .withDescription("Error fetching countries: " + e.getMessage())
          .asRuntimeException());
    }
  }

  @Transactional(readOnly = true)
  @Override
  public void geoTitleAndCountryId(GeoTitleAndCountryIdRequest request, StreamObserver<GeoResponse> responseObserver) {
    if (request.getCountryId().isEmpty() || request.getCity().isEmpty()) {
      throw new GrpcBadRequestException(
          "geoTitleAndCountryId: GeoTitleAndCountryIdRequest передано c некорректным id "
              + request.getCountryId() + " и/или c некорректным citi " + request.getCity()
      );
    }
    try {
      final GeoEntity geoEntity = geoRepository.findByCityAndCountryId(request.getCity(), UUID.fromString(request.getCountryId()))
          .orElseThrow(() ->
              new GrpcNotFoundException(
                  "Геолокация не найдена по city: " + request.getCity() +
                      " и countryId: " + request.getCountryId())
          );

      responseObserver.onNext(
          GeoResponse.newBuilder()
              .setId(geoEntity.getId().toString())
              .setCity(geoEntity.getCity())
              .setCountry(
                  CountryResponse.newBuilder()
                      .setId(geoEntity.getCountry().getId().toString())
                      .setName(geoEntity.getCountry().getName())
                      .build())
              .build()
      );
      responseObserver.onCompleted();
    } catch (IllegalArgumentException e) {
      throw new GrpcBadRequestException("Некорректный формат UUID: " + request.getCountryId());
    }
  }


  @Transactional
  @Override
  public void addGeo(GeoRequest request, StreamObserver<GeoResponse> responseObserver) {
    if (request.getCity().isEmpty() || request.getCountry().getId().isEmpty()) {
      throw new GrpcBadRequestException("addGeo: GeoRequest передано с дефолтными значениями " + request);
    }
    final CountryEntity countryEntity = countryRepository.findById(UUID.fromString(request.getCountry().getId()))
        .orElseThrow(() -> new GrpcNotFoundException("Страна не найдена по данному id: " + request.getCountry().getId()));
    final GeoEntity geoEntity = geoRepository.save(new GeoEntity(null, request.getCity(), countryEntity));

    responseObserver.onNext(
        GeoResponse.newBuilder()
            .setId(geoEntity.getId().toString())
            .setCity(geoEntity.getCity())
            .setCountry(
                CountryResponse.newBuilder()
                    .setId(geoEntity.getCountry().getId().toString())
                    .setName(geoEntity.getCountry().getName())
                    .build()
            ).build()
    );
    responseObserver.onCompleted();
  }
}