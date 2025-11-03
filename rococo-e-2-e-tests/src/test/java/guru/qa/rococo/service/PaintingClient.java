package guru.qa.rococo.service;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public interface PaintingClient {

  RestResponsePage<PaintingJson> getPaintings(
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort,
      @Nullable String title
  );

  RestResponsePage<PaintingJson> getPaintingsByAuthorId(
      @Nullable Integer page,
      @Nullable Integer size,
      @Nullable String sort,
      @NonNull UUID authorId);

  PaintingJson createPainting(@NonNull PaintingJson museumJson);

  PaintingJson updatePainting(@NonNull PaintingJson museumJson);
}
