package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.allure.AllureResult;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.service.api.AllureApiClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

public class AllureResultsDockerExtension implements SuiteExtension {
  private static final boolean isDocker = "docker".equals(System.getProperty("test.env"));
  private static final AllureApiClient allureApiClient = new AllureApiClient();
  private static final String PROJECT_ID = Config.getInstance().projectId();
  private static final Path pathToResults = Path.of("./rococo-e-2-e-tests/build/allure-results");
  private static final Logger LOG = LoggerFactory.getLogger(AllureResultsDockerExtension.class);
  private boolean allureBroken = false;

  private static final long MAX_FILE_SIZE_BYTES = 30 * 1024 * 1024;

  @Override
  public void beforeSuite(ExtensionContext context) {
    if (isDocker) {
      try {

        LOG.info("Initializing Allure project {}", PROJECT_ID);
        allureApiClient.createProject(PROJECT_ID);
        allureApiClient.cleanResults(PROJECT_ID);
      } catch (Throwable e) {
        allureBroken = true;
        // do nothing
      }
    }
  }

  @Override
  public void afterSuite() {
    if (isDocker && !allureBroken) {
      LOG.info("Processing Allure results");

      try {
        if (!Files.exists(pathToResults)) {
          LOG.error("Allure results directory not found: {}", pathToResults.toAbsolutePath());
          return;
        }

        List<Path> allFiles = getAllResultFiles();
        LOG.info("Found {} files to process", allFiles.size());

        if (allFiles.isEmpty()) {
          LOG.warn("No files to send to Allure");
          return;
        }

        processFiles(allFiles);
        allureApiClient.generateReport(PROJECT_ID);
        LOG.info("Allure processing completed");

      } catch (Exception e) {
        LOG.error("Error processing Allure results: {}", e.getMessage());
      }
    }
  }

  private List<Path> getAllResultFiles() throws IOException {
    try (Stream<Path> fileStream = Files.walk(pathToResults)) {
      return fileStream
          .filter(Files::isRegularFile)
          .toList();
    }
  }

  private void processFiles(List<Path> allFiles) {
    int successCount = 0;
    int skipCount = 0;
    int errorCount = 0;

    for (Path filePath : allFiles) {
      try {
        long fileSize = Files.size(filePath);

        if (fileSize > MAX_FILE_SIZE_BYTES) {
          skipCount++;
          continue;
        }

        if (fileSize == 0) {
          skipCount++;
          continue;
        }

        boolean success = processAndSendSingleFile(filePath);
        if (success) {
          successCount++;
        } else {
          errorCount++;
        }

      } catch (Exception e) {
        errorCount++;
      }
    }

    LOG.info("Processed: {} success, {} errors, {} skipped, {} total",
        successCount, errorCount, skipCount, allFiles.size());
  }

  private boolean processAndSendSingleFile(Path filePath) {
    try {
      String encodedContent = encodeFileToBase64(filePath);
      if (encodedContent == null || encodedContent.isEmpty()) {
        return false;
      }

      AllureResult singleResult = new AllureResult(encodedContent, filePath.getFileName().toString());
      List<AllureResult> singleFileList = List.of(singleResult);
      AllureResults singleFileResults = new AllureResults(singleFileList);

      allureApiClient.uploadResults(PROJECT_ID, singleFileResults);
      return true;

    } catch (Exception e) {
      return false;
    }
  }

  private String encodeFileToBase64(Path filePath) {
    try {
      byte[] fileBytes = Files.readAllBytes(filePath);
      return Base64.getEncoder().encodeToString(fileBytes);
    } catch (Exception e) {
      return null;
    }
  }
}