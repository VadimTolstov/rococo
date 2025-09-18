package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity category);

    Optional<CategoryEntity> findById(UUID id);

    List<CategoryEntity> findAll();

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    CategoryEntity update(CategoryEntity category);

    void delete(UUID id);
}
