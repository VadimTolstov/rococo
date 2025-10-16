package guru.qa.rococo.data.repository;

import jakarta.persistence.EntityManager;
import lombok.NonNull;
import org.springframework.util.CollectionUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public interface HibernateRepository<T> {

  EntityManager em();

  Class<T> getEntityClass();

  @NonNull
  default T create(T entity) {
    em().joinTransaction();
    em().persist(entity);
    return entity;
  }

  @NonNull
  default T update(T entity) {
    em().joinTransaction();
    em().merge(entity);
    return entity;
  }

  default void remove(T entity) {
    em().joinTransaction();
    em().remove(em().contains(entity) ? entity : em().merge(entity));
  }

  @NonNull
  default Optional<T> findByParam(Object param, String paramName) {
    final String query = String.format(
        "SELECT e FROM %s e WHERE e.%s = :value",
        getEntityClass().getSimpleName(),
        paramName
    );
    return em().createQuery(query, getEntityClass())
        .setParameter("value", param)
        .getResultStream()
        .findFirst();
  }

  @NonNull
  default List<T> findAllById(List<UUID> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return Collections.emptyList();
    }

    final String query = String.format(
        "SELECT e FROM %s e WHERE e.id IN (:uuids)",
        getEntityClass().getSimpleName()
    );
    return em().createQuery(query, getEntityClass())
        .setParameter("uuids", getUuids(uuids))
        .getResultList();
  }

  default void removeByUuidList(List<UUID> uuids) {
    if (CollectionUtils.isEmpty(uuids)) {
      return;
    }

    em().joinTransaction();
    String query = String.format(
        "DELETE FROM %s e WHERE e.id IN :uuids",
        getEntityClass().getSimpleName()
    );
    em().createQuery(query)
        .setParameter("uuids", getUuids(uuids))
        .executeUpdate();
  }

  private List<UUID> getUuids(List<UUID> uuids) {
    return uuids.stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }
}