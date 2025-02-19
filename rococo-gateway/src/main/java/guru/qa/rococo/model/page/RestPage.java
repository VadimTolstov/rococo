package guru.qa.rococo.model.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с постраничными данными в REST API.
 * Этот класс расширяет {@link PageImpl} и используется для сериализации и десериализации
 * постраничных данных, возвращаемых из REST API.
 *
 * @param <T> Тип элементов, содержащихся на странице.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestPage<T> extends PageImpl<T> {

  /**
   * Конструктор для десериализации JSON в объект {@link RestPage}.
   * Используется Jackson для создания объекта из JSON.
   *
   * @param content           Список элементов на странице.
   * @param number            Номер текущей страницы.
   * @param size              Размер страницы (количество элементов на странице).
   * @param totalElements     Общее количество элементов.
   * @param pageable          JSON-узел, содержащий информацию о пагинации (игнорируется).
   * @param last              Флаг, указывающий, является ли эта страница последней.
   * @param totalPages        Общее количество страниц.
   * @param sort              JSON-узел, содержащий информацию о сортировке (игнорируется).
   * @param first             Флаг, указывающий, является ли эта страница первой.
   * @param numberOfElements  Количество элементов на текущей странице.
   */
  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public RestPage(@JsonProperty("content") List<T> content,
                  @JsonProperty("number") int number,
                  @JsonProperty("size") int size,
                  @JsonProperty("totalElements") Long totalElements,
                  @JsonProperty("pageable") JsonNode pageable,
                  @JsonProperty("last") boolean last,
                  @JsonProperty("totalPages") int totalPages,
                  @JsonProperty("sort") JsonNode sort,
                  @JsonProperty("first") boolean first,
                  @JsonProperty("numberOfElements") int numberOfElements) {

    super(content, PageRequest.of(number, size), totalElements);
  }

  /**
   * Конструктор для создания объекта {@link RestPage} с указанным содержимым, пагинацией и общим количеством элементов.
   *
   * @param content   Список элементов на странице.
   * @param pageable  Объект, содержащий информацию о пагинации.
   * @param total     Общее количество элементов.
   */
  public RestPage(List<T> content, Pageable pageable, long total) {
    super(content, pageable, total);
  }

  /**
   * Конструктор для создания объекта {@link RestPage} с указанным содержимым.
   * Используется, если общее количество элементов неизвестно.
   *
   * @param content Список элементов на странице.
   */
  public RestPage(List<T> content) {
    super(content);
  }

  /**
   * Конструктор по умолчанию для создания пустой страницы.
   */
  public RestPage() {
    super(new ArrayList<>());
  }
}