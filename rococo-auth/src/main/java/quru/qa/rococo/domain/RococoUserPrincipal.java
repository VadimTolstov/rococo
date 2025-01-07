package quru.qa.rococo.domain;

import quru.qa.rococo.data.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Класс, представляющий принципала (principal) пользователя в системе безопасности Spring Security.
 * Этот класс реализует интерфейс {@link UserDetails}, который используется Spring Security
 * для хранения информации о пользователе, включая его учетные данные и права доступа.
 * <p>
 * Класс оборачивает сущность {@link UserEntity} и предоставляет методы для получения информации
 * о пользователе, такой как его имя, пароль, статус аккаунта и права доступа.
 */
public class RococoUserPrincipal implements UserDetails {

    // Сущность пользователя, которую представляет этот принципал
    private final UserEntity user;

    /**
     * Конструктор для создания принципала на основе сущности пользователя.
     *
     * @param user Сущность пользователя, которую нужно обернуть в принципал.
     */
    public RococoUserPrincipal(UserEntity user) {
        this.user = user;
    }

    /**
     * Возвращает список прав доступа (authorities), назначенных пользователю.
     * Права доступа преобразуются в объекты {@link SimpleGrantedAuthority}, которые
     * используются Spring Security для управления доступом.
     *
     * @return Коллекция прав доступа пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority().name()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return Пароль пользователя.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает имя пользователя (логин).
     *
     * @return Имя пользователя.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Проверяет, не истек ли срок действия аккаунта пользователя.
     *
     * @return true, если срок действия аккаунта не истек, иначе false.
     */
    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNonExpired();
    }

    /**
     * Проверяет, не заблокирован ли аккаунт пользователя.
     *
     * @return true, если аккаунт не заблокирован, иначе false.
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    /**
     * Проверяет, не истек ли срок действия учетных данных пользователя.
     *
     * @return true, если срок действия учетных данных не истек, иначе false.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNonExpired();
    }

    /**
     * Проверяет, активен ли аккаунт пользователя.
     *
     * @return true, если аккаунт активен, иначе false.
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}