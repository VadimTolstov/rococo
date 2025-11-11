package guru.qa.rococo.service.api;

import guru.qa.rococo.api.MuseumApi;
import guru.qa.rococo.api.core.RequestExecutor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.museum.CountryJson;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import io.qameta.allure.Step;
import lombok.NonNull;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MuseumApiClient implements MuseumClient, RequestExecutor {
  private static final Config CFG = Config.getInstance();

  private final MuseumApi museumApi;

  public MuseumApiClient() {
    museumApi = new RestClient.EmtyRestClient(
        CFG.museumUrl(),
        HttpLoggingInterceptor.Level.BODY
    ).create(MuseumApi.class);
  }

  /**
   * Получает список стран с поддержкой пагинации и сортировки.
   * Выполняет GET-запрос к API для получения перечня доступных стран, которые могут быть
   * использованы при создании или обновлении музеев.
   *
   * @param page номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort параметр сортировки в формате "fieldName,direction" (например, "name,asc").
   *             Если null, используется сортировка по умолчанию
   * @return объект {@link RestResponsePage} содержащий список стран {@link CountryJson} и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или параметры запроса невалидны
   * @see RestResponsePage
   * @see CountryJson
   */
  @Step("Получаем список стран с пагинацией page = {page}, size = {size}, sort = {sort}")
  @Override
  public RestResponsePage<CountryJson> getCountries(@Nullable Integer page,
                                                    @Nullable Integer size,
                                                    @Nullable String sort) {
    return executePage(museumApi.getCountries(page, size, sort), HttpStatus.SC_OK);
  }

  /**
   * Получает информацию о музее по указанному идентификатору.
   * Выполняет GET-запрос к API для получения полных данных конкретного музея.
   *
   * @param id уникальный идентификатор музея в формате UUID
   * @return объект {@link MuseumJson} с полными данными музея
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или музей не найден
   * @throws NullPointerException           если переданный id равен null
   */
  @Step("Получения музея по id = {id}")
  @Override
  public @NonNull MuseumJson getMuseumById(@NonNull UUID id) {
    return execute(museumApi.getMuseumById(id), HttpStatus.SC_OK);
  }

  /**
   * Получает список музеев с поддержкой фильтрации, пагинации и сортировки.
   * Выполняет поиск музеев по различным критериям с возможностью разбивки на страницы.
   * Поддерживает фильтрацию по названию музея (частичное совпадение, case-insensitive).
   *
   * @param page  номер страницы (начинается с 0). Если null, используется значение по умолчанию
   * @param size  количество элементов на странице. Если null, используется значение по умолчанию
   * @param sort  параметр сортировки в формате "fieldName,direction" (например, "title,asc" или "city,desc").
   *              Если null, используется сортировка по умолчанию
   * @param title опциональный параметр для фильтрации музеев по названию. Если null, фильтрация по названию не применяется
   * @return объект {@link RestResponsePage} содержащий список музеев {@link MuseumJson} и мета-информацию о пагинации
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой или параметры запроса невалидны
   * @see RestResponsePage
   * @see MuseumJson
   */
  @Step("Получаем список музеев с пагинацией page = {page}, size = {size}, sort = {sort}, title = {title}")
  @Override
  public @NonNull RestResponsePage<MuseumJson> getMuseums(@Nullable Integer page,
                                                          @Nullable Integer size,
                                                          @Nullable String sort,
                                                          @Nullable String title) {
    return executePage(museumApi.getMuseums(page, size, sort, title), HttpStatus.SC_OK);
  }

  /**
   * Создает новый музей в системе.
   * Отправляет POST-запрос к API с данными нового музея для сохранения.
   * Музей должен содержать обязательные поля: название, описание, город, страну и фото.
   *
   * @param museumJson объект {@link MuseumJson} с данными создаваемого музея.
   *                   Должен содержать обязательные поля (title, description, city, country, photo)
   * @return созданный объект {@link MuseumJson} с присвоенным идентификатором и данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, данные невалидны или музей с таким названием уже существует
   * @throws NullPointerException           если переданный museumJson равен null
   */
  @Step("Создаем музей = {museumJson}")
  @Override
  public @NonNull MuseumJson createMuseum(@NonNull MuseumJson museumJson) {
    return execute(museumApi.createMuseum(museumJson), HttpStatus.SC_OK);
  }

  /**
   * Обновляет данные существующего музея в системе.
   * Отправляет PATCH-запрос к API с обновленными данными музея.
   * Обновляет только переданные поля, сохраняя остальные данные неизменными.
   * Для идентификации музея используется его идентификатор, который должен быть установлен в переданном объекте.
   *
   * @param museumJson объект {@link MuseumJson} с обновляемыми данными музея.
   *                   Должен содержать идентификатор существующего музея и поля для обновления
   * @return обновленный объект {@link MuseumJson} с актуальными данными из системы
   * @throws guru.qa.rococo.ex.ApiException если запрос завершился ошибкой, музей не найден или данные невалидны
   * @throws NullPointerException           если переданный museumJson равен null
   * @throws IllegalArgumentException       если идентификатор музея отсутствует или имеет неверный формат
   */
  @Step("Обновляем данные музея = {museumJson}")
  @Override
  public @NonNull MuseumJson updateMuseum(@NonNull MuseumJson museumJson) {
    return execute(museumApi.updateMuseum(museumJson), HttpStatus.SC_OK);
  }

  @Override
  public void remove(@NonNull UUID id) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }

  @Override
  public void removeList(@NonNull List<UUID> uuidList) {
    throw new UnsupportedOperationException("Can`t remove artist using API");
  }
}
