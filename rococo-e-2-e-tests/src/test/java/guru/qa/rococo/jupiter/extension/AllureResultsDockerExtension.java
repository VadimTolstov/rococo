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
import java.util.ArrayList;
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

  private static final long MAX_FILE_SIZE_BYTES = 40 * 1024 * 1024;

  @Override
  public void beforeSuite(ExtensionContext context) {
    if (isDocker) {
      try {
        LOG.info("Инициализация проекта Allure {}", PROJECT_ID);
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
      LOG.info("Обработка результатов Allure");

      try {
        if (!Files.exists(pathToResults)) {
          LOG.error("Каталог результатов Allure не найден: {}", pathToResults.toAbsolutePath());
          return;
        }

        List<Path> allFiles = getAllResultFiles();
        LOG.info("Найдено {} файлов для обработки", allFiles.size());

        if (allFiles.isEmpty()) {
          LOG.warn("Нет файлов для отправки в Allure");
          return;
        }

        processFiles(allFiles);
        allureApiClient.generateReport(PROJECT_ID);
        LOG.info("Обработка Allure завершена");

      } catch (Exception e) {
        LOG.error("Ошибка обработки результатов Allure: {}", e.getMessage());
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
    int errorCount = 0;
    long batchSize = 0;
    boolean isSuccess;
    final List<Path> batch = new ArrayList<>();

    for (Path filePath : allFiles) {
      try {
        long fileSize = Files.size(filePath);
        if (batchSize + fileSize > MAX_FILE_SIZE_BYTES && !batch.isEmpty()) {
          if (processAndSendSingleFile(batch)) {
            successCount++;
          } else {
            errorCount++;
          }
          batch.clear();
          batchSize = 0;
        }
        if (fileSize <= MAX_FILE_SIZE_BYTES) {
          batch.add(filePath);
          batchSize += fileSize;
        } else {
          errorCount++;
        }
      } catch (Exception e) {
        errorCount++;
      }
    }
    if (!batch.isEmpty()) {
      LOG.info("Отправка последних файлов");
      isSuccess = processAndSendSingleFile(batch);
      if (isSuccess) {
        successCount++;
      } else {
        errorCount++;
      }
    }
    LOG.info("Processed: {} success, {} errors,  {} total ",
        successCount, errorCount, allFiles.size());
  }

  private boolean processAndSendSingleFile(List<Path> filePath) {
    final List<AllureResult> singleFileList = new ArrayList<>();
    for (Path path : filePath) {
      try {
        final String encodedContent = encodeFileToBase64(path);
        if (encodedContent == null || encodedContent.isEmpty()) {
          return false;
        }
        singleFileList.add(new AllureResult(encodedContent, path.getFileName().toString()));
      } catch (Exception e) {
        return false;
      }
    }
    LOG.info("Отправка файлов");
    allureApiClient.uploadResults(PROJECT_ID, new AllureResults(singleFileList));
    LOG.info("Отправка файлов завершена");
    return true;
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