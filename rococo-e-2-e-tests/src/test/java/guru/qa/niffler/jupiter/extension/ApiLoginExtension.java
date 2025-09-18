package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookiesStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.List;

import static guru.qa.niffler.api.core.TokenName.JSESSIONID;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.*;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final UserApiService userApiService = new UserApiService();
    private final SpendApiService spendApiService = new SpendApiService();
    private final static Config CFG = Config.getInstance();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    private ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(@NotNull ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {

                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.getUserJson();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalArgumentException("Если указана пустой @ApiLogin то должна быть указана @User");
                        }
                        userToLogin = userFromUserExtension;
                    } else {

                        // Получаем друзей и фильтруем по состоянию
                        List<UserJson> friends = userApiService
                                .friends(apiLogin.username(), null)
                                .stream()
                                .filter(user -> user.friendshipStatus() == FRIEND)
                                .toList();

                        // Получаем входящие приглашения
                        List<UserJson> incomeInvitations = friends.stream()
                                .filter(user -> user.friendshipStatus() == INVITE_RECEIVED)
                                .toList();

                        // Получаем исходящие приглашения
                        List<UserJson> outcomeInvitations = userApiService
                                .allUsers(apiLogin.username(), null)
                                .stream()
                                .filter(user -> user.friendshipStatus() == INVITE_SENT)
                                .toList();

                        // Получаем категории и траты
                        List<CategoryJson> categories = spendApiService.getAllCategories(apiLogin.username(), false);
                        categories.addAll(spendApiService.getAllCategories(apiLogin.username(), true));
                        List<SpendJson> spends = spendApiService.getAllSpends(apiLogin.username(), null, null, null);

                        UserJson fakeUser = new UserJson(
                                apiLogin.username(),
                                new TestData(
                                        apiLogin.password(),
                                        categories,
                                        spends,
                                        incomeInvitations,
                                        outcomeInvitations,
                                        friends
                                )
                        );
                        if (userFromUserExtension != null) {
                            throw new IllegalArgumentException("Если указали username and password то мы не должны генерировать юзера");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }


                    final String token = userApiService.singIn(
                            userToLogin.username(),
                            userToLogin.testData().password()
                    );
                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(getJsessionIdCookie());
                        Selenide.open(MainPage.URL, MainPage.class).checkThatPageLoaded();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestsMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestsMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                JSESSIONID.getCookieName(),
                ThreadSafeCookiesStore.INSTANCE.cookieValue(JSESSIONID.getCookieName())
        );
    }
}
