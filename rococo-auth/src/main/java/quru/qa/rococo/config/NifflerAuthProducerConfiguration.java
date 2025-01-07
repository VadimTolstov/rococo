package quru.qa.rococo.config;

import quru.qa.rococo.model.UserJson;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.DefaultSslBundleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

/**
 * Конфигурационный класс для настройки Kafka Producer.
 * Этот класс определяет конфигурацию для отправки сообщений в Kafka, включая настройки сериализации,
 * создание фабрики Producer и KafkaTemplate, а также создание топика для отправки сообщений.
 * <p>
 * Аннотация @Configuration указывает, что этот класс является конфигурационным и содержит
 * определения бинов, которые будут управляться Spring-контейнером.
 */
@Configuration
public class NifflerAuthProducerConfiguration {

    // KafkaProperties предоставляет настройки Kafka, такие как адреса брокеров, SSL и другие параметры.
    private final KafkaProperties kafkaProperties;

    // Конструктор для внедрения зависимости KafkaProperties через Spring DI.
    @Autowired
    public NifflerAuthProducerConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Создает конфигурацию для Kafka Producer.
     * Этот метод возвращает Map с настройками, которые будут использоваться для создания Producer.
     * Включает настройки сериализации ключа и значения.
     *
     * @return Map<String, Object> — конфигурационные свойства для Kafka Producer.
     */
    @Bean
    public Map<String, Object> producerConfiguration() {
        // Создаем Map для хранения конфигурационных свойств Kafka Producer.
        // Используем настройки из KafkaProperties и добавляем SSL конфигурацию через DefaultSslBundleRegistry.
        Map<String, Object> properties = new HashMap<>(kafkaProperties.buildProducerProperties(
                new DefaultSslBundleRegistry()  // SSL конфигурация для безопасного соединения с Kafka.
        ));

        // Устанавливаем сериализатор для ключа сообщения. Ключ будет сериализован как строка.
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Устанавливаем сериализатор для значения сообщения. Значение будет сериализовано в JSON.
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return properties;  // Возвращаем готовую конфигурацию для Kafka Producer.
    }

    /**
     * Создает фабрику ProducerFactory, которая будет использоваться для создания Kafka Producer.
     *
     * @return ProducerFactory<String, UserJson> — фабрика для создания Kafka Producer.
     */
    @Bean
    public ProducerFactory<String, UserJson> producerFactory() {
        // Создаем фабрику ProducerFactory, используя конфигурацию из метода producerConfiguration().
        // ProducerFactory отвечает за создание экземпляров Kafka Producer.
        return new DefaultKafkaProducerFactory<>(producerConfiguration());
    }

    /**
     * Создает KafkaTemplate для отправки сообщений в Kafka.
     * KafkaTemplate использует фабрику Producer, созданную методом producerFactory().
     *
     * @return KafkaTemplate<String, UserJson> — KafkaTemplate для отправки сообщений.
     */
    @Bean
    public KafkaTemplate<String, UserJson> kafkaTemplate() {
        // Создаем KafkaTemplate, используя ProducerFactory.
        // KafkaTemplate предоставляет удобный API для отправки сообщений в Kafka.
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Создает новый топик в Kafka для отправки сообщений.
     * Топик называется "users", имеет 10 партиций и 1 реплику.
     * Аннотация @Primary указывает, что этот бин будет использоваться по умолчанию,
     * если существует несколько бинов одного типа.
     *
     * @return NewTopic — объект, Новый топик в Kafka.
     */
    @Bean
    @Primary  // Указывает, что этот бин будет основным, если есть несколько бинов одного типа.
    public NewTopic topic() {
        // Создаем новый топик с именем "users".
        // Топик будет иметь 10 партиций и 1 реплику.
        return TopicBuilder.name("users")
                .partitions(10)  // Количество партиций в топике.
                .replicas(1)     // Количество реплик для каждой партиции.
                .build();        // Создаем и возвращаем объект NewTopic.
    }
}