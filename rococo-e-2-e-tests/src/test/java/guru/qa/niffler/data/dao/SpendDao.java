package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    SpendEntity create(SpendEntity spend);

    Optional<SpendEntity> findById(UUID id);

    List<SpendEntity> findAll();

    List<SpendEntity> findByCategoryId(UUID categoryId);

    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    SpendEntity update(SpendEntity spend);

    void delete(UUID id);
}
