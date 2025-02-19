package guru.qa.rococo.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import guru.qa.rococo.data.UserEntity;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
