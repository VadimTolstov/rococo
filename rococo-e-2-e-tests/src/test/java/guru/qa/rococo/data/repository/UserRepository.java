package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.userdata.UserEntity;
import guru.qa.rococo.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class UserRepository implements HibernateRepository<UserEntity> {

  private static final Config CFG = Config.getInstance();

  private final EntityManager em = EntityManagers.em(CFG.userdataJdbcUrl());

  @Override
  public EntityManager em() {
    return em;
  }

  @Override
  public Class<UserEntity> getEntityClass() {
    return UserEntity.class;
  }

  public Optional<UserEntity> findByUsername(String username) {
    return findByParam(username, "username");
  }
}
