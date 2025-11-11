package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class MuseumRepository implements HibernateRepository<MuseumEntity> {
  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManagers.em(CFG.museumJdbcUrl());

  @Override
  public EntityManager em() {
    return em;
  }

  @Override
  public Class<MuseumEntity> getEntityClass() {
    return MuseumEntity.class;
  }

  public Optional<MuseumEntity> findByTitle(String title) {
    return findByParam(title, "title");
  }
}
