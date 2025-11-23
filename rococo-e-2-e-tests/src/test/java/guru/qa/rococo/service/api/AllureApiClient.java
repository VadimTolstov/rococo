package guru.qa.rococo.service.api;

import guru.qa.rococo.api.AllureApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.model.allure.Project;
import guru.qa.rococo.model.allure.ProjectResponse;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AllureApiClient implements RequestExecutor {

  private final AllureApi allureApi;

  private static final Config CFG = Config.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(AllureApiClient.class);
  private static final int MAX_BATCH_SIZE_BYTES = 5 * 1024 * 1024;

  public AllureApiClient() {
    allureApi = new RestClient.EmtyRestClient(
        CFG.allureDockerServiceUrl(),
        true
    ).create(AllureApi.class);
  }


  @Step("Создаем проект {projectId} для allure")
  public void createProject(String projectId) {
    executeVoid(allureApi.createProject(new Project(projectId)), 201);
  }


  @Step("Отправляем результаты тестов в allure по проекту {projectId}")
  public void sendResults(String projectId, @Param(mode = Parameter.Mode.HIDDEN) AllureResults allureResults) {
    LOG.info("Preparing to send {} allure results for project {}", allureResults.results().size(), projectId);
    executeVoid(allureApi.sendResults(projectId, allureResults), 200);
  }


  @Step("Сгенерируем отчет по проекту {projectId}")
  public void generateReport(String projectId,
                             String executionName,
                             String executionFrom,
                             String executionType) {
    executeVoid(allureApi.generateReport(projectId, executionName, executionFrom, executionType), 200);
  }

  @Step("Получаем список проектов allure")
  public ProjectResponse getProjectsMap() {
    return execute(allureApi.getProjects(), 200);
  }

  @Step("Очищаем результаты тестов в allure по проекту {projectId}")
  public void cleanResults(String projectId) {
    executeVoid(allureApi.cleanResults(projectId), 200);
  }

  public boolean isProjectExists(String projectId) {
    return getProjectsMap()
        .data()
        .projects()
        .containsKey(projectId);
  }
}
