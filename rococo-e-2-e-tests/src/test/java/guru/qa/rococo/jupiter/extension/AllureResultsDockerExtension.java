package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.allure.AllureResult;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.service.api.AllureApiClient;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureResultsDockerExtension implements SuiteExtension {
  private static final boolean isDocker = "docker".equals(System.getProperty("test.env"));
  private static final AllureApiClient allureApiClient = new AllureApiClient();
  private static final String PROJECT_ID = Config.getInstance().projectId();
  private static final Path pathToResults = Path.of("./rococo-e-2-e-tests/build/allure-results");

  @Override
  public void beforeSuite(ExtensionContext context) {
    if (isDocker) {
      allureApiClient.createProject(PROJECT_ID);
      allureApiClient.cleanResults(PROJECT_ID);
    }
  }

  @Override
  public void afterSuite() {
    if (isDocker) {
      final Base64.Encoder encoder = Base64.getEncoder();
      final List<AllureResult> results = new ArrayList<>();

      try (Stream<Path> allureResults = Files.walk(pathToResults)) {
        allureResults
            .filter(Files::isRegularFile)
            .forEach(filePath -> {
              try (InputStream is = Files.newInputStream(filePath)) {
                results.add(
                    new AllureResult(
                        filePath.getFileName().toString(),
                        encoder.encodeToString(is.readAllBytes())
                    )
                );
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });

        allureApiClient.uploadResults(
            PROJECT_ID,
            new AllureResults(results)
        );

        allureApiClient.generateReport(PROJECT_ID);

      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}