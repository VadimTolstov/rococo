package guru.qa.rococo.api.gateway;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.UUID;

public interface ArtistGatewayApi {

  @GET("/api/artist/{id}")
  Call<ArtistJson> getArtistById(@Path("id") UUID id);

  @GET("/api/artist")
  Call<RestResponsePage<ArtistJson>> getAllArtists(
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort,
      @Query("name") String name
  );

  @POST("/api/artist")
  Call<ArtistJson> addArtist(@Body ArtistJson artist, @Header("Authorization") String bearerToken);

  @PATCH("/api/artist")
  Call<ArtistJson> updateArtist(@Body ArtistJson artist, @Header("Authorization") String bearerToken);
}