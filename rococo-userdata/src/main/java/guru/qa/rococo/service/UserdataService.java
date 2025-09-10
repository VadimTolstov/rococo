package guru.qa.rococo.service;

import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.ex.SameUsernameException;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
public class UserdataService {
    private static final Logger LOG = LoggerFactory.getLogger(UserdataService.class);

    private final UserdataRepository userdataRepository;

    @Autowired
    public UserdataService(UserdataRepository userdataRepository) {
        this.userdataRepository = userdataRepository;
    }

    /**
     * Обработчик Kafka-сообщений для создания новых пользователей.
     *
     * @param user объект пользователя из Kafka-сообщения
     * @param cr   полная запись ConsumerRecord для логирования
     */
    @Transactional
    @KafkaListener(
            topics = "users",  // Название топика Kafka
            groupId = "userdata"  // Идентификатор группы потребителей
            //containerFactory ="kafkaListenerContainerFactory" // (подразумевается по умолчанию)
    )
    public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
        // Поиск существующего пользователя по имени
        userdataRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        // Если пользователь существует
                        u -> {
                            // Логирование пропуска события
                            LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString());
                        },
                        // Если пользователь не найден
                        () -> {
                            // Логирование полученного Kafka-события
                            LOG.info("### Kafka consumer record: {}", cr.toString());

                            // Создание нового объекта пользователя
                            UserEntity userDataEntity = new UserEntity();
                            // Установка имени пользователя из сообщения
                            userDataEntity.setUsername(user.username());
                            // Сохранение пользователя в БД
                            UserEntity userEntity = userdataRepository.save(userDataEntity);
                            // Логирование успешного сохранения
                            LOG.info("### User '{}' successfully saved to database with id: {}",
                                    user.username(),
                                    userEntity.getId()
                            );
                        }
                );
    }

    @Transactional
    public @Nonnull UserJson update(@Nonnull UserJson user) {
        if (user.id() == null) {
            throw new BadRequestException("id: ID пользователя обязателен для обновления данных о пользователе");
        }

        UserEntity userEntity = userdataRepository.findById(user.id())
                .orElseThrow(() -> new NotFoundException("id: Пользователь не найден по id: " + user.id()));

        if (user.username() != null && !Objects.equals(user.username(), userEntity.getUsername())) {
            userdataRepository.findByUsername(user.username()).ifPresent(u -> {
                throw new SameUsernameException("username: Имя пользователя '" + user.username() + "' уже занято.");
            });
            userEntity.setUsername(user.username());
        }

        userEntity.setFirstname(user.firstname() != null ? user.firstname() : userEntity.getFirstname());
        userEntity.setLastname(user.lastname() != null ? user.lastname() : userEntity.getLastname());

        if (user.avatar() != null) {
            userEntity.setAvatar(user.avatar().getBytes(StandardCharsets.UTF_8));
        }

        return UserJson.fromEntity(userdataRepository.save(userEntity));
    }

    @Transactional(readOnly = true)
    public @Nonnull UserJson getUser(@Nonnull String username) {
        if (username.isBlank()) {
            throw new BadRequestException("Username не должен быть пустой или содержать одни пробелы");
        }
        return userdataRepository.findByUsername(username)
                .map(UserJson::fromEntity).orElseThrow(
                        () -> new NotFoundException("Пользователь с username = '" + username + "' не найден.")
                );
    }
}