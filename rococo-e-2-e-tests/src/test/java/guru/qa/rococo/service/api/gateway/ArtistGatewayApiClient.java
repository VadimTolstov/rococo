package guru.qa.rococo.service.api.gateway;

import guru.qa.rococo.api.core.ErrorAsserter;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.gateway.ArtistGatewayApi;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.ex.ApiException;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ArtistGatewayApiClient implements RequestExecutor, ErrorAsserter {
  private static final Config CFG = Config.getInstance();

  private final ArtistGatewayApi artistApi;

  public ArtistGatewayApiClient() {
    artistApi = new RestClient.EmtyRestClient(
        CFG.gatewayUrl(),
        HttpLoggingInterceptor.Level.HEADERS
    ).create(ArtistGatewayApi.class);
  }

  /**
   * Получает информацию о художнике по указанному идентификатору.
   * Выполняет GET-запрос к API для получения данных конкретного художника.
   *
   * @param id уникальный идентификатор художника в формате строки (будет преобразован в UUID)
   * @return объект {@link ArtistJson} с данными художника
   * @throws ApiException             если запрос завершился ошибкой или художник не найден
   * @throws IllegalArgumentException если переданный id имеет неверный формат
   */
  @Step("Получения художника по id = {id}")
  public @NonNull ArtistJson getArtist(@NonNull UUID id, int statusCode) {
    return execute(artistApi.getArtistById(id), statusCode);
  }

  /**
   * Создает нового художника в системе.
   * Отправляет POST-запрос к API с данными нового художника для сохранения.
   *
   * @param artist      объект {@link ArtistJson} с данными создаваемого художника.
   *                    Должен содержать обязательные поля (name, biography, photo)
   * @param bearerToken объект {@link String} с токином авторизации.
   * @return созданный объект {@link ArtistJson} с присвоенным идентификатором и данными из системы
   * @throws ApiException если запрос завершился ошибкой, данные невалидны или художник с таким именем уже существует
   */
  @Step("Создаем художника = {artist}")
  public @NonNull ArtistJson createArtist(@NonNull ArtistJson artist, @NonNull String bearerToken, int statusCode) {
    return execute(artistApi.addArtist(artist, bearerToken), statusCode);
  }

  /**
   * Обновляет данные существующего художника в системе.
   * Отправляет PATCH-запрос к API с обновленными данными художника.
   * Обновляет только переданные поля, сохраняя остальные данные неизменными.
   *
   * @param artist      объект {@link ArtistJson} с обновляемыми данными художника.
   *                    Должен содержать идентификатор существующего художника и поля для обновления
   * @param bearerToken объект {@link String} с токином авторизации.
   * @return обновленный объект {@link ArtistJson} с актуальными данными из системы
   * @throws ApiException если запрос завершился ошибкой или данные невалидны
   */
  @Step("Обновляем данные художника = {artist}")
  public @NonNull ArtistJson updateArtist(@NonNull ArtistJson artist, @NonNull String bearerToken, int statusCode) {
    return execute(artistApi.updateArtist(artist, bearerToken), statusCode);
  }

  /**
   * Получает список художников с поддержкой фильтрации, пагинации и сортировки.
   * Выполняет поиск художников по различным критериям с возможностью разбивки на страницы.
   *
   * @param name опциональный параметр для фильтрации художников по имени (частичное совпадение, case-insensitive).
   *             Если null, фильтрация по имени не применяется
   * @param page номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort параметр сортировки в формате "fieldName,direction" (например, "name,asc" или "createdDate,desc").
   *             Если null, используется сортировка по умолчанию
   * @return объект {@link RestResponsePage} содержащий список художников и мета-информацию о пагинации
   * @throws ApiException если запрос завершился ошибкой или параметры запроса невалидны
   * @see RestResponsePage
   * @see ArtistJson
   */
  @Step("Получаем художников по name = {name}, page = {page}, size = {size}, sort = {sort}")
  public @NonNull RestResponsePage<ArtistJson> getListArtists(@Nullable String name,
                                                              @Nullable Integer page,
                                                              @Nullable Integer size,
                                                              @Nullable String sort,
                                                              int statusCode) {
    return executePage(artistApi.getAllArtists(page, size, sort, name), statusCode);
  }
}
