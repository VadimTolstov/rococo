package guru.qa.rococo.service;

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
 * Класс для логирования всех свойств приложения при его запуске.
 * Реализует интерфейс {@link ApplicationListener} для обработки события {@link ApplicationPreparedEvent},
 * которое возникает когда приложение готово к работе, но до того как начато выполнение.
 * Логирует все свойства из всех доступных источников, отмечая переопределенные значения.
 */
public class PropertiesLogger implements ApplicationListener<ApplicationPreparedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLogger.class);

    private ConfigurableEnvironment environment;
    // Флаг для обеспечения однократного выполнения логирования при инициализации
    private boolean isFirstRun = true;

    /**
     * Обрабатывает событие подготовки приложения. Вызывается один раз при старте приложения.
     *
     * @param event Событие подготовки приложения
     */
    @Override
    public void onApplicationEvent(@Nonnull ApplicationPreparedEvent event) {
        if (isFirstRun) {
            // Получение окружения из контекста приложения
            environment = event.getApplicationContext().getEnvironment();
            // Логирование свойств
            printProperties();
        }
        isFirstRun = false; // Гарантируем однократное выполнение
    }

    /**
     * Логирует все свойства из всех доступных источников.
     * Для каждого свойства проверяет, было ли оно переопределено, и выводит соответствующее сообщение.
     */
    public void printProperties() {
        // Получаем все перечисляемые источники свойств
        for (EnumerablePropertySource<?> propertySource : findPropertiesPropertySources()) {
            LOG.info("******* {} *******", propertySource.getName());
            // Получаем и сортируем имена свойств для удобочитаемости
            String[] propertyNames = propertySource.getPropertyNames();
            Arrays.sort(propertyNames);

            for (String propertyName : propertyNames) {
                // Значение свойства с учетом всех переопределений
                String resolvedProperty = environment.getProperty(propertyName);
                // Оригинальное значение из источника
                String sourceProperty = Objects.requireNonNull(propertySource.getProperty(propertyName)).toString();

                // Проверка на переопределение свойства
                if (Objects.equals(resolvedProperty, sourceProperty)) {
                    LOG.info("{}={}", propertyName, resolvedProperty);
                } else {
                    LOG.info("{}={} OVERRIDDEN to {}", propertyName, sourceProperty, resolvedProperty);
                }
            }
        }
    }

    /**
     * Ищет и возвращает все источники свойств, реализующие {@link EnumerablePropertySource}.
     * Это необходимо для получения перечисляемого списка имен свойств.
     *
     * @return Список перечисляемых источников свойств
     */
    @Nonnull
    private List<EnumerablePropertySource<?>> findPropertiesPropertySources() {
        List<EnumerablePropertySource<?>> propertiesPropertySources = new LinkedList<>();
        // Обход всех доступных источников свойств
        for (PropertySource<?> propertySource : environment.getPropertySources()) {
            // Фильтрация только тех источников, которые позволяют перечисление свойств
            if (propertySource instanceof EnumerablePropertySource) {
                propertiesPropertySources.add((EnumerablePropertySource<?>) propertySource);
            }
        }
        return propertiesPropertySources;
    }
}