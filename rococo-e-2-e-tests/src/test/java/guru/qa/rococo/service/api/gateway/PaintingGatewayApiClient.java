package guru.qa.rococo.service.api.gateway;

import guru.qa.rococo.api.core.ErrorAsserter;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.gateway.PaintingGatewayApi;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PaintingGatewayApiClient implements RequestExecutor, ErrorAsserter {
  private static final Config CFG = Config.getInstance();

  private final PaintingGatewayApi paintingApi;

  public PaintingGatewayApiClient() {
    paintingApi = new RestClient.EmtyRestClient(
        CFG.gatewayUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(PaintingGatewayApi.class);
  }

  /**
   * Получает список картин с поддержкой пагинации, сортировки и фильтрации по названию.
   * Выполняет GET-запрос к API для получения страницы с картинами.
   *
   * @param page  номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size  количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort  параметр сортировки в формате "fieldName,direction" (например, "title,asc").
   *              Если null, используется сортировка по умолчанию
   * @param title опциональный параметр для фильтрации картин по названию (частичное совпадение).
   *              Если null, фильтрация по названию не применяется
   * @return объект {@link RestResponsePage} содержащий список картин и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или параметры запроса невалидны
   * @see RestResponsePage
   * @see PaintingJson
   */
  @Step("Получаем список картин по page = {page}, page = {page}, size = {size}, sort = {sort}, title = {title}")
  public @NonNull RestResponsePage<PaintingJson> getPaintings(@Nullable Integer page,
                                                              @Nullable Integer size,
                                                              @Nullable String sort,
                                                              @Nullable String title,
                                                              int statusCode) {
    return executePage(paintingApi.getAllPaintings(page, size, sort, title), statusCode);
  }

  /**
   * Получает список картин конкретного художника с поддержкой пагинации и сортировки.
   * Выполняет поиск всех картин, созданных указанным художником.
   *
   * @param page     номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size     количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort     параметр сортировки в формате "fieldName,direction". Если null, используется сортировка по умолчанию
   * @param authorId уникальный идентификатор художника, чьи картины необходимо найти
   * @return объект {@link RestResponsePage} содержащий список картин художника и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, параметры невалидны или художник не найден
   * @throws NullPointerException           если authorId равен null
   * @see RestResponsePage
   * @see PaintingJson
   */
  @Step("Получаем список картин по authorId = {authorId}, page = {page}, page = {page}, size = {size}, sort = {sort}, title = {title}")
  public @NonNull RestResponsePage<PaintingJson> getPaintingsByAuthorId(@NonNull UUID authorId,
                                                                        @Nullable Integer page,
                                                                        @Nullable Integer size,
                                                                        @Nullable String sort,
                                                                        int statusCode) {
    return executePage(paintingApi.getPaintingsByAuthorId(authorId, page, size, sort), statusCode);
  }

  /**
   * Создает новую картину в системе.
   * Отправляет POST-запрос к API с данными новой картины для сохранения.
   *
   * @param paintingJson объект {@link PaintingJson} с данными создаваемой картины.
   *                     Должен содержать обязательные поля (title, description, content, artistId)
   * @param bearerToken  объект {@link String} с токином авторизации.
   * @return созданный объект {@link PaintingJson} с присвоенным идентификатором и данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, данные невалидны
   *                                        или картина с таким названием уже существует
   * @throws NullPointerException           если переданный paintingJson равен null
   */
  @Step("Создаем картину = {paintingJson}")
  public @NonNull PaintingJson createPainting(@NonNull PaintingJson paintingJson, @Nullable String bearerToken, int statusCode) {
    return execute(paintingApi.addPainting(paintingJson, bearerToken), statusCode);
  }

  /**
   * Обновляет данные существующей картины в системе.
   * Отправляет PATCH-запрос к API с обновленными данными картины.
   * Обновляет только переданные поля, сохраняя остальные данные неизменными.
   *
   * @param paintingJson объект {@link PaintingJson} с обновляемыми данными картины.
   *                     Должен содержать идентификатор существующей картины и поля для обновления
   * @param bearerToken  объект {@link String} с токином авторизации.
   * @return обновленный объект {@link PaintingJson} с актуальными данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или данные невалидны
   * @throws NullPointerException           если переданный paintingJson равен null
   * @throws IllegalArgumentException       если идентификатор картины отсутствует или имеет неверный формат
   */
  @Step("Обновляем данные картины = {paintingJson}")
  public @NonNull PaintingJson updatePainting(@NonNull PaintingJson paintingJson, @Nullable String bearerToken, int statusCode) {
    return execute(paintingApi.updatePainting(paintingJson, bearerToken), statusCode);
  }
}
