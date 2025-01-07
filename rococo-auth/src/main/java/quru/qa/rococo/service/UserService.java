package quru.qa.rococo.service;

import quru.qa.rococo.data.Authority;
import quru.qa.rococo.data.AuthorityEntity;
import quru.qa.rococo.data.UserEntity;
import quru.qa.rococo.data.repository.UserRepository;
import quru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с пользователями.
 * Этот класс предоставляет методы для регистрации пользователей и управления их данными.
 * <p>
 * Аннотация @Component указывает, что этот класс является Spring-компонентом,
 * который будет управляться Spring-контейнером.
 */
@Component
public class UserService {

    // Логгер для записи событий
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    // Репозиторий для работы с данными пользователей
    private final UserRepository userRepository;

    // Кодировщик паролей
    private final PasswordEncoder passwordEncoder;

    // Kafka-шаблон для отправки сообщений
    private final KafkaTemplate<String, UserJson> kafkaTemplate;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userRepository  Репозиторий для работы с данными пользователей.
     * @param passwordEncoder Кодировщик паролей.
     * @param kafkaTemplate   Kafka-шаблон для отправки сообщений.
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       KafkaTemplate<String, UserJson> kafkaTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Регистрирует нового пользователя.
     * Этот метод создает нового пользователя, устанавливает его учетные данные и права доступа,
     * сохраняет его в базе данных и отправляет сообщение в Kafka.
     *
     * @param username Имя пользователя (логин).
     * @param password Пароль пользователя.
     * @return Имя зарегистрированного пользователя.
     */
    @Transactional
    public @Nonnull
    String registerUser(@Nonnull String username, @Nonnull String password) {
        // Создаем новую сущность пользователя
        UserEntity userEntity = new UserEntity();
        userEntity.setEnabled(true); // Устанавливаем аккаунт как активный
        userEntity.setAccountNonExpired(true); // Устанавливаем, что срок действия аккаунта не истек
        userEntity.setCredentialsNonExpired(true); // Устанавливаем, что срок действия учетных данных не истек
        userEntity.setAccountNonLocked(true); // Устанавливаем, что аккаунт не заблокирован
        userEntity.setUsername(username); // Устанавливаем имя пользователя
        userEntity.setPassword(passwordEncoder.encode(password)); // Кодируем пароль и устанавливаем его

        // Создаем права доступа для пользователя
        AuthorityEntity readAuthorityEntity = new AuthorityEntity();
        readAuthorityEntity.setAuthority(Authority.read); // Право на чтение
        AuthorityEntity writeAuthorityEntity = new AuthorityEntity();
        writeAuthorityEntity.setAuthority(Authority.write); // Право на запись

        // Добавляем права доступа пользователю
        userEntity.addAuthorities(readAuthorityEntity, writeAuthorityEntity);

        // Сохраняем пользователя в базе данных
        String savedUser = userRepository.save(userEntity).getUsername();

        // Отправляем сообщение в Kafka о новом пользователе
        kafkaTemplate.send("users", new UserJson(savedUser));
        LOG.info("### Kafka topic [users] sent message: {}", savedUser);

        // Возвращаем имя зарегистрированного пользователя
        return savedUser;
    }
}