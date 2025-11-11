package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class ArtistRepository implements HibernateRepository<ArtistEntity> {
  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManagers.em(CFG.artistJdbcUrl());

  @Override
  public EntityManager em() {
    return em;
  }

  @Override
  public Class<ArtistEntity> getEntityClass() {
    return ArtistEntity.class;
  }

  public Optional<ArtistEntity> findByName(String title) {
    return findByParam(title, "name");
  }
}
