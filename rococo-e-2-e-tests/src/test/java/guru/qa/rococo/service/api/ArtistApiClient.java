package guru.qa.rococo.service.api;

import guru.qa.rococo.api.ArtistApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.ex.ApiException;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.artist.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ArtistApiClient implements ArtistClient, RequestExecutor {
  private static final Config CFG = Config.getInstance();

  private final ArtistApi artistApi;

  public ArtistApiClient() {
    artistApi = new RestClient.EmtyRestClient(
        CFG.artistUrl(),
        HttpLoggingInterceptor.Level.NONE
    ).create(ArtistApi.class);
  }

  /**
   * Получает информацию о художнике по указанному идентификатору.
   * Выполняет GET-запрос к API для получения данных конкретного художника.
   *
   * @param id уникальный идентификатор художника в формате строки (будет преобразован в UUID)
   * @return объект {@link ArtistJson} с данными художника
   * @throws ApiException если запрос завершился ошибкой или художник не найден
   * @throws IllegalArgumentException если переданный id имеет неверный формат
   */
  @Step("Получения художника по id = {id}")
  @Override
  public @NonNull ArtistJson getArtist(@NonNull UUID id) {
    return execute(artistApi.getArtist(id), HttpStatus.SC_OK);
  }

  /**
   * Создает нового художника в системе.
   * Отправляет POST-запрос к API с данными нового художника для сохранения.
   *
   * @param artist объект {@link ArtistJson} с данными создаваемого художника.
   *               Должен содержать обязательные поля (name, biography, photo)
   * @return созданный объект {@link ArtistJson} с присвоенным идентификатором и данными из системы
   * @throws ApiException если запрос завершился ошибкой, данные невалидны или художник с таким именем уже существует
   * @throws NullPointerException если переданный artist равен null
   */
  @Step("Создаем художника = {artist}")
  @Override
  public @NonNull ArtistJson createArtist(@NonNull ArtistJson artist) {
    return execute(artistApi.createArtist(artist), HttpStatus.SC_OK);
  }

  /**
   * Обновляет данные существующего художника в системе.
   * Отправляет PATCH-запрос к API с обновленными данными художника.
   * Обновляет только переданные поля, сохраняя остальные данные неизменными.
   *
   * @param artist объект {@link ArtistJson} с обновляемыми данными художника.
   *               Должен содержать идентификатор существующего художника и поля для обновления
   * @return обновленный объект {@link ArtistJson} с актуальными данными из системы
   * @throws ApiException если запрос завершился ошибкой или данные невалидны
   * @throws NullPointerException если переданный artist равен null
   * @throws IllegalArgumentException если идентификатор художника отсутствует или имеет неверный формат
   */
  @Step("Обновляем данные художника = {artist}")
  @Override
  public @NonNull ArtistJson updateArtist(@NonNull ArtistJson artist) {
    return execute(artistApi.updateArtist(artist), HttpStatus.SC_OK);
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
  @Override
  public @NonNull RestResponsePage<ArtistJson> getPageListArtists(@Nullable String name,
                                                                  @Nullable Integer page,
                                                                  @Nullable Integer size,
                                                                  @Nullable String sort) {

    return executePage(artistApi.getArtists(name, page, size, sort), HttpStatus.SC_OK);
  }

  @Override
  public List<ArtistJson> getListArtists(@NonNull List<UUID> uuidList) {
    throw new UnsupportedOperationException("Can`t getListArtists artist using API");
  }

  @Override
  public void remove(@NonNull ArtistJson artist) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }

  @Override
  public void removeList(@NonNull List<UUID> uuidList) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }
}
