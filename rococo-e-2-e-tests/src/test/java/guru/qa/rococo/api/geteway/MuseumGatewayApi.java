package guru.qa.rococo.api.geteway;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.UUID;

public interface MuseumGatewayApi {

  @GET("/api/museum/{id}")
  Call<MuseumJson> getMuseumById(@Path("id") UUID id);

  @GET("/api/museum")
  Call<RestResponsePage<MuseumJson>> getAllMuseums(
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort,
      @Query("title") String title
  );

  @GET("/api/country")
  Call<RestResponsePage<CountryJson>> getAllCountries(
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort
  );

  @POST("/api/museum")
  Call<MuseumJson> addMuseum(@Body MuseumJson museum, @Header("Authorization") String bearerToken);

  @PATCH("/api/museum")
  Call<MuseumJson> updateMuseum(@Body MuseumJson museum, @Header("Authorization") String bearerToken);
}