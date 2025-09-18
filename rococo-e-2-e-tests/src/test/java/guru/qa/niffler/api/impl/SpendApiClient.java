package guru.qa.niffler.api.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.ex.ApiException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.api.core.RestClient;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient extends RestClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        spendApi = retrofit.create(SpendApi.class);
    }

    @Step("Send POST [internal/spends/add] to niffler-spend")
    public @Nonnull SpendJson addSpend(@Nonnull SpendJson spend) {
        return execute(spendApi.addSpend(spend), HttpStatus.SC_CREATED);
    }

    @Step("Send GET [internal/spends/{id}] to niffler-spend")
    public @Nonnull SpendJson getSpend(@Nonnull String id,
                                       @Nonnull String username) {
        return execute(spendApi.getSpend(id, username), HttpStatus.SC_OK);
    }

    @Step("Send GET [internal/spends/all] to niffler-spend")
    public @Nonnull List<SpendJson> getAllSpends(@Nonnull String username,
                                                 @Nullable CurrencyValues filterCurrency,
                                                 @Nullable Date from,
                                                 @Nullable Date to) {
        Response<List<SpendJson>> response = executeForList(
                spendApi.getAllSpends(username, filterCurrency, from, to)
        );
        return getList(response);
    }


    @Step("Send PATCH [internal/spends/edit] to niffler-spend")
    public @Nonnull SpendJson editSpend(@Nonnull SpendJson spend) {
        return execute(spendApi.editSpend(spend), HttpStatus.SC_OK);
    }

    @Step("Send DELETE [internal/spends/remove] to niffler-spend")
    public void deleteSpends(@Nonnull String username,
                             @Nonnull List<String> ids) {
        executeVoid(spendApi.deleteSpends(username, ids));
    }

    @Step("Send GET [/internal/categories/all] to niffler-spend")
    public @Nonnull List<CategoryJson> getAllCategories(@Nonnull String username,
                                                        boolean excludeArchived) {
        Response<List<CategoryJson>> response = executeForList(
                spendApi.getAllCategories(username, excludeArchived)
        );
        return getList(response);
    }

    @Step("Send POST [/internal/categories/add] to niffler-spend")
    public @Nonnull CategoryJson addCategory(@Nonnull CategoryJson category) {
        return execute(spendApi.addCategory(category), HttpStatus.SC_OK);
    }

    @Step("Send PATCH [/internal/categories/update] to niffler-spend")
    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        return execute(spendApi.updateCategory(category), HttpStatus.SC_OK);
    }

    // Общие методы для выполнения запросов
    private @Nonnull <T> T execute(@Nonnull Call<T> call, int expectedStatusCode) {
        try {
            final Response<T> response = call.execute();
            assertEquals(expectedStatusCode, response.code());
            return Objects.requireNonNull(
                    response.body(),
                    "Ответ API вернул null для " + call.request().method() + " " + call.request().url());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private <T> Response<T> executeForList(@Nonnull Call<T> call) {
        try {
            final Response<T> response = call.execute();
            assertEquals(HttpStatus.SC_OK, response.code());
            return response;
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }

    }

    private void executeVoid(@Nonnull Call<Void> call) {
        try {
            final Response<Void> response = call.execute();
            assertEquals(HttpStatus.SC_ACCEPTED, response.code());
        } catch (IOException e) {
            throw new ApiException("Ошибка выполнения запроса", e);
        }
    }

    private @Nonnull <T> List<T> getList(@Nonnull Response<List<T>> response) {
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
