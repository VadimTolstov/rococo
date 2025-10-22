package guru.qa.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.service.UserdataClient;
import guru.qa.rococo.service.api.AuthApiClient;
import guru.qa.rococo.service.api.UserdataApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

  private static final Config CFG = Config.getInstance();
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final UserdataClient userdataApiClient = new UserdataApiClient();
  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  public ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension rest() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
        .ifPresent(apiLogin -> {

          final UserJson userToLogin;
          final UserJson userFromUserExtension = UserExtension.getUser();
          if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
            if (userFromUserExtension == null) {
              throw new IllegalStateException("Если мы указали пустой @ApiLogin то @User обязательно должен быть над тестом!");
            }
            userToLogin = userFromUserExtension;
          } else {
            //todo если мы хотим залогиниться уже существующем пользователем? или всегда делаем нового
            //todo передали пользователя мы его создаем ? или он должен быть в системе уже?
//            UserJson userJson = userdataApiClient.getUser(apiLogin.username());
//            if (userJson == null) {
//
//            }

//            final UserJson fakeUser = authApiClient.createUser(
//                apiLogin.username(),
//                apiLogin.password()
//            ).withPassword(apiLogin.password());

            final UserJson fakeUser = UserJson.builder()
                .username(apiLogin.username())
                .password(apiLogin.password())
                .build();

            if (userFromUserExtension != null) {
              throw new IllegalStateException("@User не должен быть над тестом если у  @ApiLogin заполнен username или password!");
            }
            UserExtension.setUser(fakeUser);
            userToLogin = fakeUser;
          }

          final String token = authApiClient.singIn(
              userToLogin.username(),
              userToLogin.password()
          );
          setToken(token);
          if (setupBrowser) {
            Selenide.open(CFG.frontUrl());
            Selenide.localStorage().setItem("id_token", getToken());
            WebDriverRunner.getWebDriver().manage().addCookie(
                getJsessionIdCookie()
            );
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
    return "Bearer " + getToken();
  }

  public static void setToken(String token) {
    context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(String code) {
    context().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return context().getStore(NAMESPACE).get("code", String.class);
  }

  public static Cookie getJsessionIdCookie() {
    return new Cookie(
        "JSESSIONID",
        ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
    );
  }
}
