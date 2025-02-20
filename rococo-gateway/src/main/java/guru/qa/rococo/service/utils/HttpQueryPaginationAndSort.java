package guru.qa.rococo.service.utils;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Утилита для преобразования параметров пагинации и сортировки в параметры HTTP-запроса.
 * <p>
 * Формирует параметры:
 * <ul>
 *   <li>{@code page} - номер страницы</li>
 *   <li>{@code size} - размер страницы</li>
 *   <li>{@code sort} - параметры сортировки (могут повторяться)</li>
 * </ul>
 */
public class HttpQueryPaginationAndSort {
    private final Pageable pageable;

    public HttpQueryPaginationAndSort(@Nonnull Pageable pageable) {
        this.pageable = pageable;
    }

    /**
     * Преобразует параметры в MultiValueMap для использования в URI.
     *
     * @return карта параметров запроса
     */
    public MultiValueMap<String, String> toQueryParams() {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        // Добавляем базовые параметры пагинации
        queryParams.add("page", String.valueOf(pageable.getPageNumber()));
        queryParams.add("size", String.valueOf(pageable.getPageSize()));

        // Добавляем параметры сортировки
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                queryParams.add(
                        "sort",
                        order.getProperty() + "," + order.getDirection().name().toLowerCase()
                );
            }
        }
        return queryParams;
    }
}