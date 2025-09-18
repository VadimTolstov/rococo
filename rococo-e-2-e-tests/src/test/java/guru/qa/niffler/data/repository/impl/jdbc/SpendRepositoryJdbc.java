package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        return spendDao.create(spend);
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        return spendDao.update(spend);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
        return spendDao.findById(id)
                .map(spend -> {
                    Optional.ofNullable(spend.getCategory())
                            .map(CategoryEntity::getId)
                            .flatMap(categoryDao::findById)
                            .ifPresent(spend::setCategory);
                    return spend;
                });
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndSpendDescription(@Nonnull String username, @Nonnull String description) {
        return spendDao.findByUsernameAndSpendDescription(username, description);
    }

    @Override
    public void remove(@Nonnull SpendEntity spend) {
        spendDao.delete(spend.getId());
    }

    @Override
    public @Nonnull CategoryEntity createCategory(@Nonnull CategoryEntity category) {
        return categoryDao.findCategoryByUsernameAndCategoryName(
                category.getUsername(),
                category.getName()
        ).orElseGet(() -> categoryDao.create(category));
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        return categoryDao.findById(id);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndSpendName(@Nonnull String username, @Nonnull String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public void removeCategory(@Nonnull CategoryEntity category) {
        List<SpendEntity> spendEntityList = spendDao.findByCategoryId(category.getId());
        for (SpendEntity spend : spendEntityList) {
            spendDao.delete(spend.getId());
        }
        categoryDao.delete(category.getId());
    }
}
