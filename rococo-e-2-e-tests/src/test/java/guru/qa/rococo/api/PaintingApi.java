package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.UUID;

public interface PaintingApi {

    @GET("/internal/painting/{paintingId}")
    Call<PaintingJson> getPainting(@Path("paintingId") @NonNull UUID paintingId);

    @GET("/internal/painting")
    Call<RestResponsePage<PaintingJson>> getPaintings(
            @Query("page") @Nullable Integer page,
            @Query("size") @Nullable Integer size,
            @Query("sort") @Nullable String sort,
            @Query("title") @Nullable String title
    );

    @GET("/internal/painting/author/{authorId}")
    Call<RestResponsePage<PaintingJson>> getPaintingsByAuthorId(
            @Query("page") @Nullable Integer page,
            @Query("size") @Nullable Integer size,
            @Query("sort") @Nullable String sort,
            @Path("authorId") @NonNull UUID authorId
    );

    @POST("/internal/painting")
    Call<PaintingJson> addPainting(@Body @NonNull PaintingJson paintingJson);

    @PATCH("/internal/painting")
    Call<PaintingJson> patchPainting(@Body @NonNull PaintingJson paintingJson);

}
