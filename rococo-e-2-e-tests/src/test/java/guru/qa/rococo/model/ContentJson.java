package guru.qa.rococo.model;

import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;

public record ContentJson(
    @Nonnull Set<ArtistJson> artists,
    @Nonnull Set<MuseumJson> museums,
    @Nonnull Set<PaintingJson> paintings
) {

}
