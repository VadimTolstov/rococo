package guru.qa.rococo.service.api;

import guru.qa.rococo.ex.NoGrpcResponseException;
import guru.qa.rococo.ex.NoRestResponseException;
import guru.qa.rococo.model.GeoJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.model.page.RestPage;
import guru.qa.rococo.service.utils.HttpQueryPaginationAndSort;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

/**
 * REST-клиент для работы с микросервисом музеев.
 * Обеспечивает управление данными о музеях и странах.
 */
@Component
public class RestMuseumClient {
  private final RestTemplate restTemplate;
  private final String rococoMuseumBaseUri;
  private final GrpcGeoClient grpcGeoClient;

  @Autowired
  public RestMuseumClient(RestTemplate restTemplate,
                          @Value("${rococo-museum.base-uri}") String rococoMuseumBaseUri,
                          GrpcGeoClient grpcGeoClient) {
    this.restTemplate = restTemplate;
    this.rococoMuseumBaseUri = rococoMuseumBaseUri + "/internal";
    this.grpcGeoClient = grpcGeoClient;
  }

  /**
   * Получает страницу музеев с фильтрацией по названию.
   *
   * @param pageable Параметры пагинации
   * @param title    Фильтр по названию (может быть null)
   * @return Страница музеев {@link Page<MuseumJson>}
   * @throws NoRestResponseException Если ответ отсутствует
   */
  public @Nonnull Page<MuseumJson> getAllMuseums(@Nonnull Pageable pageable,
                                                 @Nullable String title) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder
        .fromUriString(rococoMuseumBaseUri)
        .path("/museum")
        .queryParams(new HttpQueryPaginationAndSort(pageable).toQueryParams());

    if (StringUtils.hasText(title)) {
      uriBuilder.queryParam("title", title.trim());
    }

    URI uri = uriBuilder.encode(StandardCharsets.UTF_8).build().toUri();

    ResponseEntity<RestPage<MuseumJson>> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        }
    );

    return Optional.ofNullable(response.getBody())
        .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum GET]"));
  }

  public @Nonnull MuseumJson getMuseumById(@Nonnull UUID id) {
    URI uri = UriComponentsBuilder
        .fromUriString(rococoMuseumBaseUri)
        .path("/museum/{id}")
        .buildAndExpand(id)
        .toUri();

    ResponseEntity<MuseumJson> response = restTemplate.exchange(
        uri,
        HttpMethod.GET,
        null,
        MuseumJson.class
    );

    MuseumJson museumResponse = Optional.ofNullable(response.getBody())
        .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum/{id} GET]"));
    final GeoJson geoJson = grpcGeoClient.getGeoById(museumResponse.geo().id());

    return museumResponse.setGeo(geoJson);
  }

  /**
   * Добавляет новый музей.
   *
   * @param museum Данные музея
   * @return Созданный музей {@link MuseumJson}
   * @throws NoRestResponseException При ошибке связи с сервисом
   */
  public @Nonnull MuseumJson addMuseum(@Nonnull MuseumJson museum) {
    final GeoJson geoJson = grpcGeoClient.addGeo(museum.geo());
    if (geoJson.id() != null) {
      museum = museum.setGeo(geoJson);

      URI uri = UriComponentsBuilder
          .fromUriString(rococoMuseumBaseUri)
          .path("/museum")
          .build()
          .toUri();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      HttpEntity<MuseumJson> request = new HttpEntity<>(museum, headers);

      ResponseEntity<MuseumJson> response = restTemplate.exchange(
          uri,
          HttpMethod.POST,
          request,
          MuseumJson.class
      );
      return Optional.ofNullable(response.getBody())
          .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum POST]"));
    }
    throw new NoGrpcResponseException("No Grpc response is addGeo geo: " + museum.geo());
  }

  public @Nonnull MuseumJson updateMuseum(@Nonnull MuseumJson museum) {
    URI uri = UriComponentsBuilder
        .fromUriString(rococoMuseumBaseUri)
        .path("/museum")
        .build()
        .toUri();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<MuseumJson> request = new HttpEntity<>(museum, headers);

    ResponseEntity<MuseumJson> response = restTemplate.exchange(
        uri,
        HttpMethod.PATCH,
        request,
        MuseumJson.class
    );

    return Optional.ofNullable(response.getBody())
        .orElseThrow(() -> new NoRestResponseException("No REST response is given [/internal/museum PATCH]"));
  }
}