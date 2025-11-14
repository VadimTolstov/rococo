package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.model.ContentJson;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.db.ArtistDbClient;
import guru.qa.rococo.service.db.MuseumDbClient;
import guru.qa.rococo.service.db.PaintingDbClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class ContentExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ContentExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          setContent(new ContentJson(
              new HashSet<>(),
              new HashSet<>(),
              new HashSet<>()
          ));
        });
  }


  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    final ContentJson content = getContent();
    if (content != null) {
      final List<UUID> paintingsList = new ArrayList<>(content.paintings().stream().map(PaintingJson::id).distinct().toList());
      final List<UUID> artistsList = new ArrayList<>(content.artists().stream().map(ArtistJson::id).distinct().toList());
      final List<UUID> museumList = new ArrayList<>(content.museums().stream().map(MuseumJson::id).distinct().toList());
      if (!paintingsList.isEmpty()) {
        new PaintingDbClient().removeList(paintingsList);
      }
      if (!artistsList.isEmpty()) {
        new ArtistDbClient().removeList(artistsList);
      }
      if (!museumList.isEmpty()) {
        new MuseumDbClient().removeList(museumList);
      }
    }
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