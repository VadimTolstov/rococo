package guru.qa.rococo.service;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public interface MuseumClient {

  RestResponsePage<CountryJson> getCountries(
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort);

  MuseumJson getMuseumById(@NonNull UUID id);

  RestResponsePage<MuseumJson> getMuseums(
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort,
      @Nullable String title
  );

  MuseumJson createMuseum(@NonNull MuseumJson museumJson);

  MuseumJson updateMuseum(@NonNull MuseumJson museumJson);
}
