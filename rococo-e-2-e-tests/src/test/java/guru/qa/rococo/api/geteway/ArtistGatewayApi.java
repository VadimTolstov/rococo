package guru.qa.rococo.api.geteway;

import guru.qa.rococo.model.rest.artist.ArtistJson;
import org.springframework.data.domain.Page;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.UUID;

public interface ArtistGatewayApi {

  @GET("/api/artist/{id}")
  Call<ArtistJson> getArtistById(@Path("id") UUID id);

  @GET("/api/artist")
  Call<Page<ArtistJson>> getAllArtists(
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort,
      @Query("name") String name
  );

  @POST("/api/artist")
  Call<ArtistJson> addArtist(@Body ArtistJson artist, @Header("Authorization") String token);

  @PATCH("/api/artist")
  Call<ArtistJson> updateArtist(@Body ArtistJson artist, @Header("Authorization") String token);
}