package guru.qa.rococo.service;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.Serializable;

/**
 * Фильтр для логирования запросов, соответствующих определенным URL-шаблонам.
 * Этот фильтр оборачивает другой фильтр (декоратор) и применяет его только к запросам,
 * которые соответствуют указанным URL-шаблонам.
 * <p>
 * Фильтр реализует интерфейсы {@link Filter}, {@link FilterConfig} и {@link Serializable}.
 */
public class SpecificRequestDumperFilter extends GenericFilter implements Filter, FilterConfig, Serializable {

    // URL-шаблоны, для которых будет применяться фильтр
    private final String[] urlPatterns;

    // Фильтр-декоратор, который будет применяться к запросам
    private final GenericFilter decorate;

    /**
     * Конструктор для создания фильтра.
     *
     * @param decorate    Фильтр-декоратор, который будет применяться к запросам.
     * @param urlPatterns URL-шаблоны, для которых будет применяться фильтр.
     */
    public SpecificRequestDumperFilter(GenericFilter decorate, String... urlPatterns) {
        this.decorate = decorate;
        this.urlPatterns = urlPatterns;
    }

    /**
     * Основной метод фильтрации запросов.
     * Этот метод проверяет, соответствует ли запрос одному из указанных URL-шаблонов.
     * Если соответствует, применяется фильтр-декоратор. В противном случае запрос передается
     * следующему фильтру в цепочке.
     *
     * @param request  Запрос, который нужно отфильтровать.
     * @param response Ответ, который будет отправлен клиенту.
     * @param chain    Цепочка фильтров, которая позволяет передать запрос и ответ следующему фильтру.
     * @throws IOException      Если произошла ошибка ввода-вывода.
     * @throws ServletException Если произошла ошибка при обработке запроса.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Проверяем, является ли запрос HTTP-запросом
        if (request instanceof HttpServletRequest hRequest) {
            // Проверяем, соответствует ли URI запроса одному из URL-шаблонов
            for (String urlPattern : urlPatterns) {
                if (hRequest.getRequestURI().matches(urlPattern)) {
                    // Если соответствует, применяем фильтр-декоратор
                    decorate.doFilter(request, response, chain);
                    return;
                }
            }
        }
        // Если запрос не соответствует ни одному из шаблонов, передаем его следующему фильтру
        chain.doFilter(request, response);
    }

    /**
     * Метод для уничтожения фильтра.
     * Этот метод вызывает метод destroy у фильтра-декоратора.
     */
    @Override
    public void destroy() {
        decorate.destroy();
    }
}