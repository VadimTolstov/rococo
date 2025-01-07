package quru.qa.rococo.data.repository;

import quru.qa.rococo.data.UserEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link UserEntity}.
 * Этот интерфейс предоставляет методы для выполнения операций CRUD (Create, Read, Update, Delete)
 * с пользователями в базе данных, а также дополнительные методы для поиска пользователей.
 * <p>
 * Интерфейс расширяет {@link JpaRepository}, что позволяет использовать стандартные методы
 * Spring Data JPA для работы с базой данных.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Находит пользователя по его имени (логину).
     *
     * @param username Имя пользователя (логин), по которому осуществляется поиск.
     * @return {@link Optional}, содержащий найденного пользователя, если он существует,
     * или пустой {@link Optional}, если пользователь с таким именем не найден.
     */
    @Nonnull
    Optional<UserEntity> findByUsername(@Nonnull String username);
}