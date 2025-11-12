package guru.qa.rococo.service.api;

import guru.qa.rococo.api.PaintingApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import guru.qa.rococo.service.PaintingClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PaintingApiClient implements PaintingClient, RequestExecutor {
  private static final Config CFG = Config.getInstance();

  private final PaintingApi paintingApi;

  public PaintingApiClient() {
    paintingApi = new RestClient.EmtyRestClient(
        CFG.paintingUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(PaintingApi.class);
  }

  /**
   * Получает список картин с поддержкой пагинации, сортировки и фильтрации по названию.
   * Выполняет GET-запрос к API для получения страницы с картинами.
   *
   * @param page номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort параметр сортировки в формате "fieldName,direction" (например, "title,asc").
   *             Если null, используется сортировка по умолчанию
   * @param title опциональный параметр для фильтрации картин по названию (частичное совпадение).
   *              Если null, фильтрация по названию не применяется
   * @return объект {@link RestResponsePage} содержащий список картин и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или параметры запроса невалидны
   * @see RestResponsePage
   * @see PaintingJson
   */
  @Step("Получаем список картин по page = {page}, page = {page}, size = {size}, sort = {sort}, title = {title}")
  @Override
  public @NonNull RestResponsePage<PaintingJson> getPaintings(@Nullable Integer page,
                                                              @Nullable Integer size,
                                                              @Nullable String sort,
                                                              @Nullable String title) {
    return executePage(paintingApi.getPaintings(page, size, sort, title), HttpStatus.SC_OK);
  }

  /**
   * Получает список картин конкретного художника с поддержкой пагинации и сортировки.
   * Выполняет поиск всех картин, созданных указанным художником.
   *
   * @param page номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort параметр сортировки в формате "fieldName,direction". Если null, используется сортировка по умолчанию
   * @param authorId уникальный идентификатор художника, чьи картины необходимо найти
   * @return объект {@link RestResponsePage} содержащий список картин художника и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, параметры невалидны или художник не найден
   * @throws NullPointerException если authorId равен null
   * @see RestResponsePage
   * @see PaintingJson
   */
  @Step("Получаем список картин по authorId = {authorId}, page = {page}, page = {page}, size = {size}, sort = {sort}, title = {title}")
  @Override
  public @NonNull RestResponsePage<PaintingJson> getPaintingsByAuthorId(@Nullable Integer page,
                                                                        @Nullable Integer size,
                                                                        @Nullable String sort,
                                                                        @NonNull UUID authorId) {
    return executePage(paintingApi.getPaintingsByAuthorId(page, size, sort, authorId), HttpStatus.SC_OK);
  }

  /**
   * Создает новую картину в системе.
   * Отправляет POST-запрос к API с данными новой картины для сохранения.
   *
   * @param paintingJson объект {@link PaintingJson} с данными создаваемой картины.
   *                     Должен содержать обязательные поля (title, description, content, artistId)
   * @return созданный объект {@link PaintingJson} с присвоенным идентификатором и данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, данные невалидны
   *         или картина с таким названием уже существует
   * @throws NullPointerException если переданный paintingJson равен null
   */
  @Step("Создаем картину = {paintingJson}")
  @Override
  public @NonNull PaintingJson createPainting(@NonNull PaintingJson paintingJson) {
    return execute(paintingApi.addPainting(paintingJson), HttpStatus.SC_OK);
  }

  /**
   * Обновляет данные существующей картины в системе.
   * Отправляет PATCH-запрос к API с обновленными данными картины.
   * Обновляет только переданные поля, сохраняя остальные данные неизменными.
   *
   * @param paintingJson объект {@link PaintingJson} с обновляемыми данными картины.
   *                     Должен содержать идентификатор существующей картины и поля для обновления
   * @return обновленный объект {@link PaintingJson} с актуальными данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или данные невалидны
   * @throws NullPointerException если переданный paintingJson равен null
   * @throws IllegalArgumentException если идентификатор картины отсутствует или имеет неверный формат
   */
  @Step("Обновляем данные картины = {paintingJson}")
  @Override
  public @NonNull PaintingJson updatePainting(@NonNull PaintingJson paintingJson) {
    return execute(paintingApi.patchPainting(paintingJson), HttpStatus.SC_OK);
  }

  @Override
  public void remove(@NonNull PaintingJson painting) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }

  @Override
  public void removeList(@NonNull List<UUID> uuidList) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }
}
