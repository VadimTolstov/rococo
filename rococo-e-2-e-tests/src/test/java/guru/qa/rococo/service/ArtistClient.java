package guru.qa.rococo.service;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public interface ArtistClient {

  ArtistJson getArtist(@NonNull UUID id);

  ArtistJson createArtist(@NonNull ArtistJson artist);

  ArtistJson updateArtist(@NonNull ArtistJson artist);

  RestResponsePage<ArtistJson> getPageListArtists(
      @Nullable String name,
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort);

  List<ArtistJson> getListArtists(@NonNull List<UUID> uuidList);

  void remove(@NonNull ArtistJson artist);

  void removeList(@NonNull List<UUID> uuidList);
}
