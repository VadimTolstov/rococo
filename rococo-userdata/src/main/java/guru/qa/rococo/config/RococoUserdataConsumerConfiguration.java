package guru.qa.rococo.config;

import guru.qa.rococo.model.UserJson;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * Конфигурация потребителя Kafka для сервиса пользовательских данных Rococo.
 * Настраивает десериализацию сообщений и создает фабрики для обработки входящих сообщений.
 */

@Configuration // Помечает класс как конфигурационный компонент Spring
public class RococoUserdataConsumerConfiguration {

    private final KafkaProperties kafkaProperties; // Свойства Kafka из конфигурации приложения

    /**
     * Конструктор с
     * внедрением зависимостей.
     *
     * @param kafkaProperties автоматически настроенные свойства Kafka
     */

    @Autowired
    public RococoUserdataConsumerConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Создает фабрику потребителей Kafka.
     *
     * @param sslBundles SSL-настройки для безопасного подключения
     * @return фабрика потребителей для обработкисообщений
     */

    @Bean
    public ConsumerFactory<String, UserJson> consumerFactory(SslBundles sslBundles) {
        // Настройка JSON-десериализатора
        final JsonDeserializer<UserJson> jsonDeserializer = new JsonDeserializer<>();
        // Разрешение десериализации из любых пакетов (осторожно в production!)
        jsonDeserializer.addTrustedPackages("*");

        // Создание фабрики потребителей с тремя параметрами:
        return new DefaultKafkaConsumerFactory<>(
                // 1. Базовые свойства Kafka + SSL конфигурация
                kafkaProperties.buildAdminProperties(sslBundles), // Внимание: должно быть buildConsumerProperties!

                // 2. Десериализатор для ключей сообщений (String)
                new StringDeserializer(),

                // 3. Десериализатор для значений сообщений (JSON в UserJson)
                jsonDeserializer
        );
    }

    /**
     * Создает фабрику контейнеров для параллельной обработки сообщений Kafka.
     *
     * @param sslBundles SSL-настройки для безопасного подключения
     * @return фабрика слушателей сообщений
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserJson> kafkaListenerContainerFactory(SslBundles sslBundles) {
        // Создание фабрики контейнеров
        ConcurrentKafkaListenerContainerFactory<String, UserJson> concurrentKafkaListenerContainerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Настройка фабрики потребителей
        concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory(sslBundles));

        return concurrentKafkaListenerContainerFactory;
    }
}