package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.UUID;

public interface ArtistApi {

    @GET("/internal/artist/{id}")
    Call<ArtistJson> getArtist(@NonNull @Path("id") UUID id);

    @GET("/internal/artist")
    Call<RestResponsePage<ArtistJson>> getArtists(
            @Query("name") @Nullable String name,
            @Query("page") @Nullable Integer page,
            @Query("size") @Nullable Integer size,
            @Query("sort") @Nullable String sort
    );

    @POST("/internal/artist")
    Call<ArtistJson> createArtist(@NonNull @Body ArtistJson artist);

    @PATCH("/internal/artist")
    Call<ArtistJson> updateArtist(@NonNull @Body ArtistJson artist);
}
