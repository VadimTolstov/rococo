package guru.qa.rococo.api.geteway;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.UUID;

public interface PaintingGatewayApi {

  @GET("/api/painting/{id}")
  Call<PaintingJson> getPaintingById(@Path("id") UUID id);

  @GET("/api/painting")
  Call<RestResponsePage<PaintingJson>> getAllPaintings(
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort,
      @Query("title") String title
  );

  @GET("/api/painting/author/{id}")
  Call<RestResponsePage<PaintingJson>> getPaintingsByAuthorId(
      @Path("id") UUID id,
      @Query("page") Integer page,
      @Query("size") Integer size,
      @Query("sort") String sort
  );

  @POST("/api/painting")
  Call<PaintingJson> addPainting(@Body PaintingJson painting, @Header("Authorization") String bearerToken);

  @PATCH("/api/painting")
  Call<PaintingJson> updatePainting(@Body PaintingJson painting, @Header("Authorization") String bearerToken);
}