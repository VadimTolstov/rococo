package guru.qa.rococo.service;

import guru.qa.rococo.data.UserEntity;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.ex.BadRequestException;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.UserJson;
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Service
public class UserdataService {
  private static final Logger LOG = LoggerFactory.getLogger(UserdataService.class);

  private final UserdataRepository userdataRepository;

  @Autowired
  public UserdataService(UserdataRepository userdataRepository) {
    this.userdataRepository = userdataRepository;
  }

  @Transactional
  @KafkaListener(
      topics = "users",
      groupId = "userdata"
  )
  public void listener(@Payload UserJson user, ConsumerRecord<String, UserJson> cr) {
    userdataRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> {
              LOG.info("### User already exist in DB, kafka event will be skipped: {}", cr.toString());
            },
            () -> {
              LOG.info("### Kafka consumer record: {}", cr.toString());

              UserEntity userDataEntity = new UserEntity();
              userDataEntity.setUsername(user.username());
              UserEntity userEntity = userdataRepository.save(userDataEntity);
              LOG.info("### User '{}' successfully saved to database with id: {}",
                  user.username(),
                  userEntity.getId()
              );
            }
        );
  }

  @Transactional
  public @NonNull UserJson update(@NonNull UserJson user) {
    if (user.id() == null) {
      throw new BadRequestException("id: ID пользователя обязателен для обновления данных о пользователе");
    }

    UserEntity userEntity = userdataRepository.findById(user.id())
        .orElseThrow(() -> new NotFoundException("id: Пользователь не найден по id: " + user.id()));

    userEntity.setFirstname(user.firstname() != null ? user.firstname() : userEntity.getFirstname());
    userEntity.setLastname(user.lastname() != null ? user.lastname() : userEntity.getLastname());

    if (user.avatar() != null) {
      userEntity.setAvatar(user.avatar().getBytes(StandardCharsets.UTF_8));
    }

    return UserJson.fromEntity(userdataRepository.save(userEntity));
  }

  @Transactional(readOnly = true)
  public @NonNull UserJson getUser(@NonNull String username) {
    if (username.isBlank()) {
      throw new BadRequestException("Username не должен быть пустой или содержать одни пробелы");
    }
    return userdataRepository.findByUsername(username)
        .map(UserJson::fromEntity).orElseThrow(
            () -> new NotFoundException("Пользователь с username = '" + username + "' не найден.")
        );
  }
}