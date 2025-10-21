package guru.qa.rococo.model;

import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;

import javax.annotation.Nonnull;
import java.util.List;

public record TestContent(
    @Nonnull List<ArtistJson> artists,
    @Nonnull List<MuseumJson> museums,
    @Nonnull List<PaintingJson> paintings
) {

}
