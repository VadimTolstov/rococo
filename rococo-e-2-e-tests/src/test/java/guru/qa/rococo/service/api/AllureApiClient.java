package guru.qa.rococo.service.api;

import guru.qa.rococo.api.AllureApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.allure.AllureResult;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.model.allure.Project;
import guru.qa.rococo.model.allure.ProjectResponse;
import io.qameta.allure.Param;
import io.qameta.allure.Step;
import io.qameta.allure.model.Parameter;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.HttpException;

import java.util.ArrayList;
import java.util.List;


public class AllureApiClient implements RequestExecutor {

  private final AllureApi allureApi;

  private static final Config CFG = Config.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(AllureApiClient.class);

  public AllureApiClient() {
    allureApi = new RestClient.EmtyRestClient(
        CFG.allureDockerServiceUrl(),
        HttpLoggingInterceptor.Level.NONE
    ).create(AllureApi.class);
  }


 //"Создаем проект {projectId} для allure"
  public void createProject(String projectId) {
    if (isProjectExists(projectId)) {
      LOG.info("Проект {} уже существует", projectId);
      return;
    }
    executeVoid(allureApi.createProject(new Project(projectId)), 201);
    LOG.info("Создан проект {}", projectId);
  }


//"Отправляем результаты тестов в allure по проекту {projectId}"
  public void uploadResults(String projectId, @Param(mode = Parameter.Mode.HIDDEN) AllureResults allureResults) {
      executeVoid(allureApi.uploadResults(projectId, allureResults), 200);
  }

  //"Сгенерируем отчет по проекту {projectId}"
  public void generateReport(String projectId) {
    executeVoid(
        allureApi.generateReport(
            projectId,
            System.getenv("HEAD_COMMIT_MESSAGE"),
            System.getenv("BUILD_URL"),
            System.getenv("EXECUTION_TYPE")
        ), 200
    );
  }

  //"Получаем список проектов allure"
  public ProjectResponse getProjectsMap() {
    return execute(allureApi.getProjects(), 200);
  }

  //"Очищаем результаты тестов в allure по проекту {projectId}")
  public void cleanResults(String projectId) {
    executeVoid(allureApi.cleanResults(projectId), 200);
  }

  private boolean isProjectExists(String projectId) {
    return getProjectsMap()
        .data()
        .projects()
        .containsKey(projectId);
  }
}
