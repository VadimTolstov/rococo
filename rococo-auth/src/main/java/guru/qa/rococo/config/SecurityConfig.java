package guru.qa.rococo.config;

import guru.qa.rococo.service.SpecificRequestDumperFilter;
import guru.qa.rococo.service.cors.CookieCsrfFilter;
import guru.qa.rococo.service.cors.CorsCustomizer;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.DisableEncodeUrlFilter;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Конфигурационный класс для настройки безопасности приложения.
 * Этот класс определяет правила безопасности для HTTP-запросов, включая настройки CORS,
 * CSRF-защиты, аутентификации, авторизации и обработки исключений.
 * <p>
 * Аннотация @Configuration указывает, что этот класс является конфигурационным и содержит
 * определения бинов, которые будут управляться Spring-контейнером.
 */
@Configuration
public class SecurityConfig {

  // Кастомизатор CORS
  private final CorsCustomizer corsCustomizer;

  // Окружение (профили) приложения
  private final Environment environment;

  /**
   * Конструктор для внедрения зависимостей.
   *
   * @param corsCustomizer Кастомизатор CORS.
   * @param environment    Окружение (профили) приложения.
   */
  @Autowired
  public SecurityConfig(CorsCustomizer corsCustomizer, Environment environment) {
    this.corsCustomizer = corsCustomizer;
    this.environment = environment;
  }

  /**
   * Настройка SecurityFilterChain для приложения.
   * Этот метод определяет правила безопасности для HTTP-запросов, включая настройки CORS,
   * CSRF-защиты, аутентификации, авторизации и обработки исключений.
   *
   * @param http Объект HttpSecurity для настройки безопасности.
   * @return SecurityFilterChain для приложения.
   * @throws Exception Если произошла ошибка при настройке безопасности.
   */
  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    // Применяем кастомизацию CORS
    corsCustomizer.corsCustomizer(http);

    // Добавляем фильтр для логирования запросов в локальном и staging окружении
    if (environment.acceptsProfiles(Profiles.of("local", "staging"))) {
      http.addFilterBefore(new SpecificRequestDumperFilter(
          new RequestDumperFilter(),
          "/login", "/oauth2/.*"
      ), DisableEncodeUrlFilter.class);
    }

    return http.authorizeHttpRequests(customizer -> customizer
            // Разрешаем доступ к определенным URL без аутентификации
            .requestMatchers(
                antMatcher("/register"),
                antMatcher("/error"),
                antMatcher("/images/**"),
                antMatcher("/styles/**"),
                antMatcher("/fonts/**"),
                antMatcher("/actuator/health"),
                // ДОБАВЛЕНО: Разрешаем доступ к .well-known без аутентификации
                antMatcher("/.well-known/**")
            ).permitAll()
            // Все остальные запросы требуют аутентификации
            .anyRequest()
            .authenticated()
        )
        // Настройка CSRF-защиты
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
        // Добавляем фильтр для работы с CSRF-токенами
        .addFilterAfter(new CookieCsrfFilter(), BasicAuthenticationFilter.class)
        // Настройка формы входа
        .formLogin(login -> login
            .loginPage("/login")
            .permitAll())
        // Настройка выхода из системы
        .logout(logout -> logout
            .logoutRequestMatcher(antMatcher("/logout"))
            .deleteCookies("JSESSIONID", "XSRF-TOKEN")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
        )
        // Настройка обработки исключений
        .exceptionHandling(customizer -> customizer
            .accessDeniedPage("/error")
        )
        // Настройка управления сессиями
        .sessionManagement(sm -> sm.invalidSessionUrl("/login"))
        .build();
  }
}