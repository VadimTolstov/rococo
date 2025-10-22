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
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class MuseumExtension implements BeforeEachCallback, ParameterResolver {
  private static final Config CFG = Config.getInstance();
  private final String IMAGE_DIR = "museums";

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(MuseumExtension.class);
  private final MuseumClient museumClient = new MuseumApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Content.class)
        .ifPresent(content -> {
          Allure.step("Создаем художников перед тестом", () -> {
            if (ArrayUtils.isNotEmpty(content.museums()) || content.museumCount() > 0) {
              final List<MuseumJson> museums = new ArrayList<>();

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
                            museumAnno.country() == Country.RUSSIA
                                ? Country.RUSSIA.getCountry()
                                : museumAnno.country().getCountry()
                        )
                    )
                );
              }
            }
          });
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return false;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return null;
  }
}
