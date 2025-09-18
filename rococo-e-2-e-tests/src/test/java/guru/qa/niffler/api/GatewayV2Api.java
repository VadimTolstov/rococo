package guru.qa.niffler.api;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface GatewayV2Api {

    @GET("api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                @Query("page") Integer page,
                                                @Query("size") Integer size,
                                                @Query("searchQuery") @Nullable String searchQuery);

    @GET("/api/v2/spends/all")
    Call<RestResponsePage<SpendJson>> allSpends(@Header("Authorization") @Nonnull String bearerToken,
                                                @Query("page") @Nullable Integer page,
                                                @Query("filterPeriod") @Nullable DataFilterValues filterPeriod,
                                                @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                                @Query("searchQuery") @Nullable String searchQuery);

    @GET("/api/v2/users/all")
    Call<RestResponsePage<UserJson>> allUsers(@Header("Authorization") @Nonnull String bearerToken,
                                              @Query("searchQuery") @Nullable String searchQuery,
                                              @Query("page") @Nullable Integer page,
                                              @Query("size") @Nullable Integer size,
                                              @Query("sort") @Nullable String sort);
}
