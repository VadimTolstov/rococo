package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.userdata.UserJson;
import guru.qa.rococo.service.AuthClient;
import guru.qa.rococo.service.UserdataClient;
import guru.qa.rococo.service.api.AuthApiClient;
import guru.qa.rococo.service.api.UserdataApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.util.StringUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {
  private static final Config CFG = Config.getInstance();

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

  private final AuthClient authClient = new AuthApiClient();
  private final UserdataClient userdataClient = new UserdataApiClient();
  private final String PASSWORD = "12345";

  @Override
  @Step("Создаем пользователе перед началом теста")
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> Allure.step("Найдена аннотация User над тестом", () -> {
          {
            if ("".equals(userAnno.username())) {
              final String username = RandomDataUtils.randomUsername();
              final String password = "".equals(userAnno.password()) ? PASSWORD : userAnno.password();
              UserJson user = authClient.createUser(
                  username,
                  password
              ).withPassword(password);

              var userBuilder = user.toBuilder();
              boolean needToUpdate = false;

              if (!StringUtils.isBlank(userAnno.firstname())) {
                userBuilder.firstname(userAnno.firstname());
                needToUpdate = true;
              }
              if (!StringUtils.isBlank(userAnno.lastname())) {
                userBuilder.lastname(userAnno.lastname());
                needToUpdate = true;
              }
              if (!StringUtils.isBlank(userAnno.avatar())) {
                userBuilder.avatar(PhotoConverter.loadImageAsString(CFG.imageContentBaseDir() + userAnno.avatar()));
                needToUpdate = true;
              }

              if (needToUpdate) {
                user = userdataClient.updateUser(userBuilder.build()).withPassword(password);
              }

              setUser(user);
            }
          }
        }));
  }

  public static void setUser(UserJson user) {
    final ExtensionContext context = TestsMethodContextExtension.context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        user
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
      ParameterResolutionException {
    return getUser();
  }

  public static @Nullable UserJson getUser() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }
}