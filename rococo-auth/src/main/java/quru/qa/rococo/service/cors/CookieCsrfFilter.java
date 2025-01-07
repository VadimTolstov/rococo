package quru.qa.rococo.service.cors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр для установки CSRF-токена в заголовок ответа.
 * Этот фильтр решает проблему, связанную с тем, что Spring Security 6 по умолчанию не устанавливает
 * cookie с CSRF-токеном. Решение было предложено в
 * <a href="https://github.com/spring-projects/spring-security/issues/12141#issuecomment-1321345077">
 * Официальном репозитории Spring Security.</a>
 * <p>
 * Фильтр наследуется от {@link OncePerRequestFilter}, что гарантирует его выполнение один раз для каждого запроса.
 */
public class CookieCsrfFilter extends OncePerRequestFilter {

    /**
     * Метод, который выполняет основную логику фильтра.
     * Этот метод извлекает CSRF-токен из запроса и добавляет его в заголовок ответа.
     *
     * @param request     HTTP-запрос.
     * @param response    HTTP-ответ.
     * @param filterChain Цепочка фильтров, которая позволяет передать запрос и ответ следующему фильтру.
     * @throws ServletException Если произошла ошибка при обработке запроса.
     * @throws IOException      Если произошла ошибка ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Извлекаем CSRF-токен из запроса
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // Устанавливаем CSRF-токен в заголовок ответа
        response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());

        // Передаем запрос и ответ следующему фильтру в цепочке
        filterChain.doFilter(request, response);
    }
}