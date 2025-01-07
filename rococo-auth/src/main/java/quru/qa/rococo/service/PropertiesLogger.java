package quru.qa.rococo.service;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Класс для логирования свойств (properties) приложения во время его запуска.
 * Этот класс реализует интерфейс {@link ApplicationListener} и логирует все свойства,
 * которые загружены в окружение приложения, включая те, которые были переопределены.
 * <p>
 * Логирование происходит на этапе подготовки приложения (ApplicationPreparedEvent).
 */
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {

    // Логгер для записи информации о свойствах
    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLogger.class);

    // Окружение приложения, содержащее свойства
    private ConfigurableEnvironment environment;

    // Флаг для отслеживания первого запуска
    private boolean isFirstRun = true;

    /**
     * Обрабатывает событие подготовки приложения (ApplicationPreparedEvent).
     * Этот метод вызывается, когда приложение готово к запуску, и логирует все свойства.
     *
     * @param event Событие подготовки приложения.
     */
    @Override
    public void onApplicationEvent(@Nonnull ApplicationPreparedEvent event) {
        // Логируем свойства только при первом запуске
        if (isFirstRun) {
            environment = event.getApplicationContext().getEnvironment();
            printProperties();
        }
        isFirstRun = false;
    }

    /**
     * Логирует все свойства приложения.
     * Этот метод проходит по всем источникам свойств и выводит их в лог,
     * отмечая переопределенные значения.
     */
    public void printProperties() {
        // Получаем все источники свойств, которые можно перечислить
        for (EnumerablePropertySource<?> propertySource : findPropertiesPropertySources()) {
            // Логируем имя источника свойств
            LOG.info("******* {} *******", propertySource.getName());

            // Получаем и сортируем имена свойств
            String[] propertyNames = propertySource.getPropertyNames();
            Arrays.sort(propertyNames);

            // Логируем каждое свойство
            for (String propertyName : propertyNames) {
                // Получаем значение свойства из окружения
                String resolvedProperty = environment.getProperty(propertyName);
                // Получаем исходное значение свойства из источника
                String sourceProperty = Objects.requireNonNull(propertySource.getProperty(propertyName)).toString();

                // Логируем свойство, отмечая, если оно было переопределено
                if (Objects.equals(resolvedProperty, sourceProperty)) {
                    LOG.info("{}={}", propertyName, resolvedProperty);
                } else {
                    LOG.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
                }
            }
        }
    }

    /**
     * Находит все источники свойств, которые можно перечислить.
     * Этот метод возвращает список источников свойств, которые реализуют интерфейс {@link EnumerablePropertySource}.
     *
     * @return Список источников свойств, которые можно перечислить.
     */
    private List<EnumerablePropertySource<?>> findPropertiesPropertySources() {
        List<EnumerablePropertySource<?>> propertiesPropertySources = new LinkedList<>();

        // Проходим по всем источникам свойств в окружении
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            // Если источник свойств можно перечислить, добавляем его в список
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource<?>) propertySource);
            }
        }
        return propertiesPropertySources;
    }
}