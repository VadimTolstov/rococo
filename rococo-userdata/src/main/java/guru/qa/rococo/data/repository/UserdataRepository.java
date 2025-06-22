package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserdataRepository extends JpaRepository<UserEntity, UUID> {
}