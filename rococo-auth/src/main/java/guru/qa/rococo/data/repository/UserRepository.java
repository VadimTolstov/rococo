package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.UserEntity;
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

    @Nonnull
    Optional<UserEntity> findByUsername(@Nonnull String username);
}