package guru.qa.rococo.api;

import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.model.allure.Project;
import guru.qa.rococo.model.allure.ProjectResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AllureApi {

  @POST("allure-docker-service/send-results")
  Call<Void> uploadResults(@Query("project_id") String projectId,
                           @Body AllureResults allureResults);

  @GET("allure-docker-service/projects")
  Call<ProjectResponse> getProjects();

  @GET("allure-docker-service/clean-results")
  Call<Void> cleanResults(@Query("project_id") String projectId);

  @GET("allure-docker-service/generate-report")
  Call<Void> generateReport(@Query("project_id") String projectId,
                            @Query("execution_name") String executionName,
                            @Query(value = "execution_from",encoded = true) String executionFrom,
                            @Query("execution_type") String executionType);

  @POST("allure-docker-service/projects")
  Call<Void> createProject(@Body Project project);
}