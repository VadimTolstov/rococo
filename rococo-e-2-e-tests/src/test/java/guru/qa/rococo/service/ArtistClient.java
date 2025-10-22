package guru.qa.rococo.service;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import lombok.NonNull;

import javax.annotation.Nullable;

public interface ArtistClient {

  ArtistJson getArtist(@NonNull String id);

  ArtistJson createArtist(@NonNull ArtistJson artist);

  ArtistJson updateArtist(@NonNull ArtistJson artist);

  RestResponsePage<ArtistJson> getListArtists(
      @Nullable String name,
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort);
}
