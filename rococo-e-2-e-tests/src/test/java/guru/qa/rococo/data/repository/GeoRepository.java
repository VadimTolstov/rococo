package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.GeoEntity;
import guru.qa.rococo.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class GeoRepository implements HibernateRepository<GeoEntity> {
  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManagers.em(CFG.museumJdbcUrl());

  @Override
  public EntityManager em() {
    return em;
  }

  @Override
  public Class<GeoEntity> getEntityClass() {
    return GeoEntity.class;
  }

  public Optional<GeoEntity> findByName(String title) {
    return findByParam(title, "name");
  }
}
