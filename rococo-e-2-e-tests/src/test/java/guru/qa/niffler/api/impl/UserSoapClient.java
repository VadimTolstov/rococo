package guru.qa.niffler.api.impl;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.ex.SoapException;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class UserSoapClient extends RestClient {

    private final UserSoapApi soapApi;

    public UserSoapClient() {
        super(
                CFG.userdataUrl(),
                false,
                SoapConverterFactory.create("niffler-userdata"),
                HttpLoggingInterceptor.Level.BODY
        );
        soapApi = retrofit.create(UserSoapApi.class);
    }

    @Step("SOAP currentUser to niffler-userdata")
    public @Nonnull UserResponse currentUser(@Nonnull CurrentUserRequest request) {
        return execute(soapApi.currentUser(request), HttpStatus.SC_OK);
    }

    @Step("SOAP allUsers to niffler-userdata")
    public @Nonnull UsersResponse allUsers(@Nonnull AllUsersRequest request) {
        return execute(soapApi.allUsers(request), HttpStatus.SC_OK);
    }

    @Step("SOAP allUsersPage to niffler-userdata")
    public @Nonnull UsersResponse allUsersPage(@Nonnull AllUsersPageRequest request) {
        return execute(soapApi.allUsersPage(request), HttpStatus.SC_OK);
    }

    @Step("SOAP updateUserInfo to niffler-userdata")
    public @Nonnull UserResponse updateUserInfo(@Nonnull UpdateUserRequest request) {
        return execute(soapApi.updateUserInfo(request), HttpStatus.SC_OK);
    }

    @Step("SOAP friends to niffler-userdata")
    public @Nonnull UsersResponse friends(@Nonnull FriendsRequest request) {
        return execute(soapApi.friends(request), HttpStatus.SC_OK);
    }

    @Step("SOAP friendsPage to niffler-userdata")
    public @Nonnull UsersResponse friendsPage(@Nonnull FriendsPageRequest request) {
        return execute(soapApi.friendsPage(request), HttpStatus.SC_OK);
    }

    @Step("SOAP removeFriend to niffler-userdata")
    public void removeFriend(@Nonnull RemoveFriendRequest request) {
        executeVoid(soapApi.removeFriend(request));
    }

    @Step("SOAP sendInvitation to niffler-userdata")
    public @Nonnull UserResponse sendInvitation(@Nonnull SendInvitationRequest request) {
        return execute(soapApi.sendInvitation(request), HttpStatus.SC_OK);
    }


    @Step("SOAP acceptInvitation to niffler-userdata")
    public @Nonnull UserResponse acceptInvitation(@Nonnull AcceptInvitationRequest request) {
        return execute(soapApi.acceptInvitation(request), HttpStatus.SC_OK);
    }

    @Step("SOAP declineInvitation to niffler-userdata")
    public @Nonnull UserResponse declineInvitation(@Nonnull DeclineInvitationRequest request) {
        return execute(soapApi.declineInvitation(request), HttpStatus.SC_OK);
    }

    // Общие методы для выполнения запросов
    private <T> T execute(Call<T> call, int expectedStatusCode) {
        try {
            final Response<T> response = call.execute();
            if (expectedStatusCode != response.code()) {
                throw new SoapException("Unexpected status code: " + response.code()
                        + " for " + call.request().method()
                        + " " + call.request().url());
            }
            return Objects.requireNonNull(
                    response.body(),
                    "Ответ SOAP вернул null для " + call.request().method() + " " + call.request().url());
        } catch (IOException e) {
            throw new SoapException("Ошибка выполнения SOAP запроса", e);
        }
    }

    private void executeVoid(Call<Void> call) {
        try {
            final Response<Void> response = call.execute();
            if (HttpStatus.SC_ACCEPTED != response.code()) {
                throw new SoapException("Unexpected status code: " + response.code()
                        + " for " + call.request().method()
                        + " " + call.request().url());
            }
        } catch (IOException e) {
            throw new SoapException("Ошибка выполнения SOAP запроса", e);
        }
    }
}
