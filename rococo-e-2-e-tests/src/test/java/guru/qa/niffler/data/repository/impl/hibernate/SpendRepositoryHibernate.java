package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@Slf4j
public class SpendRepositoryHibernate implements SpendRepository {

    private final static Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        return entityManager.merge(spend);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(SpendEntity.class, id)
        );
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndSpendDescription(@Nonnull String username, @Nonnull String description) {
        try {
            return Optional.ofNullable(
                    entityManager.createQuery(
                                    "SELECT s FROM SpendEntity s " +
                                            "WHERE s.username = :username AND s.description = :description",
                                    SpendEntity.class
                            )
                            .setParameter("username", username)
                            .setParameter("description", description)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(spend) ? spend : entityManager.merge(spend));
    }


    @Override
    public @Nonnull CategoryEntity createCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        return entityManager.merge(category);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        return Optional.ofNullable(
                entityManager.find(CategoryEntity.class, id)
        );
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndSpendName(@Nonnull String username, @Nonnull String name) {
        try {
            return Optional.ofNullable(
                    entityManager.createQuery(
                                    "SELECT c FROM CategoryEntity c " +
                                            "WHERE c.username =: username and c.name =: name",
                                    CategoryEntity.class
                            )
                            .setParameter("username", username)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void removeCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.remove(entityManager.contains(category) ? category : entityManager.merge(category));
    }

}