package guru.qa.rococo.jupiter.extension;

import com.github.jknack.handlebars.internal.lang3.ArrayUtils;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.annotation.Content;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.model.rest.museum.Country;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.GeoJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.api.MuseumApiClient;
import guru.qa.rococo.utils.PhotoConverter;
import guru.qa.rococo.utils.RandomDataUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class MuseumExtension implements BeforeEachCallback /*ParameterResolver */ {
  private static final Config CFG = Config.getInstance();
  private final String IMAGE_DIR = "museums";

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
  private final MuseumClient museumClient = new MuseumApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          Allure.step("Музеи перед тестом", () -> {
            if (ArrayUtils.isNotEmpty(content.museums()) || content.museumCount() > 0) {
              final List<MuseumJson> createMuseums = new ArrayList<>();

              for (final Museum museumAnno : content.museums()) {
                final MuseumJson museum = new MuseumJson(
                    null,
                    "".equals(museumAnno.title())
                        ? RandomDataUtils.museum()
                        : museumAnno.title(),
                    "".equals(museumAnno.description())
                        ? RandomDataUtils.shortBio()
                        : museumAnno.description(),
                    "".equals(museumAnno.photo())
                        ? RandomDataUtils.randomImageString(IMAGE_DIR)
                        : PhotoConverter.loadImageAsString(CFG.imageContentBaseDir() + museumAnno.photo()),
                    new GeoJson(
                        "".equals(museumAnno.city())
                            ? RandomDataUtils.city()
                            : museumAnno.city(),
                        new CountryJson(
                            null,
                            museumAnno.country().getCountry()
                        )
                    )
                );
                createMuseums.add(museumClient.createMuseum(museum));
              }
              for (int i = 0; i < content.museumCount(); i++) {
                createMuseums.add(museumClient.createMuseum(
                    new MuseumJson(
                        null,
                        RandomDataUtils.museum(),
                        RandomDataUtils.shortBio(),
                        RandomDataUtils.randomImageString(IMAGE_DIR),
                        new GeoJson(
                            RandomDataUtils.city(),
                            new CountryJson(
                                null,
                                Country.RUSSIA.getCountry()
                            )
                        )
                    )
                ));
              }
              ContentExtension.getContent().museums().addAll(createMuseums);
              //   setMuseum(createMuseums);
            }
          });
        });
  }


//  @Override
//  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//    return parameterContext.getParameter().getType().isAssignableFrom(MuseumJson[].class);
//  }
//
//  @Override
//  public MuseumJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
//    return getMuseum().toArray(MuseumJson[]::new);
//  }
//
//  @Nonnull
//  @SuppressWarnings("unchecked")
//  public static List<MuseumJson> getMuseum() {
//    final ExtensionContext context = TestsMethodContextExtension.context();
//    return Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), List.class))
//        .orElse(Collections.emptyList());
//  }
//
//  public static void setMuseum(List<MuseumJson> createMuseums) {
//    final ExtensionContext context = TestsMethodContextExtension.context();
//    context.getStore(NAMESPACE).put(context.getUniqueId(), createMuseums);
//  }
}
