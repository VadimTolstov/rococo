package guru.qa.rococo.data.repository;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingRepository implements HibernateRepository<PaintingEntity> {
  private static final Config CFG = Config.getInstance();
  private final EntityManager em = EntityManagers.em(CFG.paintingJdbcUrl());

  @Override
  public EntityManager em() {
    return em;
  }

  @Override
  public Class<PaintingEntity> getEntityClass() {
    return PaintingEntity.class;
  }

  public Optional<PaintingEntity> findByTitle(String title) {
    return findByParam(title,"title");
  }

  public List<PaintingEntity> findByArtistId(UUID artistId) {
    return em.createQuery(
            "SELECT p FROM PaintingEntity p WHERE p.artist = :artistId",
            PaintingEntity.class
        )
        .setParameter("artistId", artistId)
        .getResultList();
  }
}
