package guru.qa.rococo.service;

import guru.qa.rococo.data.repository.UserRepository;
import guru.qa.rococo.domain.RococoUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для загрузки данных пользователя в Spring Security.
 * Этот класс реализует интерфейс {@link UserDetailsService}, который используется Spring Security
 * для получения информации о пользователе во время аутентификации.
 * <p>
 * Аннотация @Component указывает, что этот класс является Spring-компонентом,
 * который будет управляться Spring-контейнером.
 */
@Component
public class RococoUserDetailsService implements UserDetailsService {

    // Репозиторий для работы с данными пользователей
    private final UserRepository userRepository;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param userRepository Репозиторий для работы с данными пользователей.
     */
    @Autowired
    public RococoUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Загружает данные пользователя по его имени (логину).
     * Этот метод используется Spring Security для получения информации о пользователе
     * во время аутентификации.
     *
     * @param username Имя пользователя (логин), по которому осуществляется поиск.
     * @return Объект {@link UserDetails}, содержащий информацию о пользователе.
     * @throws UsernameNotFoundException Если пользователь с указанным именем не найден.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя в репозитории по имени
        return userRepository.findByUsername(username)
                // Если пользователь найден, создаем объект NifflerUserPrincipal
                .map(RococoUserPrincipal::new)
                // Если пользователь не найден, выбрасываем исключение
                .orElseThrow(() -> new UsernameNotFoundException("Username: `" + username + "` not found"));
    }
}