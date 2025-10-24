package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.model.ContentJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;

public class ContentExtension implements BeforeEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ContentExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          setContent(new ContentJson(
              new ArrayList<>(),
              new ArrayList<>(),
              new ArrayList<>()
          ));
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getRequiredTestMethod().isAnnotationPresent(Content.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(ContentJson.class);
  }

  @Override
  public ContentJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), ContentJson.class);
  }

  public static ContentJson getContent() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), ContentJson.class);
  }

  public static void setContent(ContentJson content) {
    final ExtensionContext context = TestsMethodContextExtension.context();
    context.getStore(NAMESPACE).put(context.getUniqueId(), content);
  }
}
