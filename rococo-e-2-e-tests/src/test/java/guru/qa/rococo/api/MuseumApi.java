package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import lombok.NonNull;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.UUID;

public interface MuseumApi {

    @GET("/internal/country")
    Call<RestResponsePage<CountryJson>> getCountries(
            @Query("page") @Nullable Integer page,
            @Query("size") @Nullable Integer size,
            @Query("sort") @Nullable String sort);

    @GET("/internal/museum/{id}")
    Call<MuseumJson> getMuseumById(@Path("id") @NonNull UUID id);

    @GET("/internal/museum")
    Call<RestResponsePage<MuseumJson>> getMuseums(
            @Query("page") @Nullable Integer page,
            @Query("size") @Nullable Integer size,
            @Query("sort") @Nullable String sort,
            @Query("title") @Nullable String title
    );

    @POST("/internal/museum")
    Call<MuseumJson> createMuseum(@Body MuseumJson museumJson);

    @PATCH("/internal/museum")
    Call<MuseumJson> updateMuseum(@Body MuseumJson museumJson);
}
