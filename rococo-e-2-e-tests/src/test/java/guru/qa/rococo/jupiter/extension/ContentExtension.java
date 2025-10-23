package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.model.ContentJson;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.util.ArrayList;

public class ContentExtension implements ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ContentExtension.class);

  public static ContentJson content() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    ContentJson content = context.getStore(NAMESPACE).get(context.getUniqueId(), ContentJson.class);
    if (content == null) {
      content = new ContentJson(
          new ArrayList<>(),
          new ArrayList<>(),
          new ArrayList<>()
      );
    }
    return content;
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(ContentJson.class) &&
        extensionContext.getRequiredTestMethod().isAnnotationPresent(Content.class);
  }

  @Override
  public ContentJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), ContentJson.class);
  }
}
