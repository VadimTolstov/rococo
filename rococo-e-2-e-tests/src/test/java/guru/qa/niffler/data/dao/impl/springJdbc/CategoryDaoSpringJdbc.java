package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.dao.impl.springJdbc.utils.DaoUtils.getGeneratedId;

public class CategoryDaoSpringJdbc implements CategoryDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull CategoryEntity create(@Nonnull CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (name, username, archived)" +
                            "VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            return ps;

        }, kh);

        final UUID generatedKey = getGeneratedId(kh, "id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<CategoryEntity> result = jdbcTemplate.query(
                "SELECT * FROM category WHERE id = ?",
                CategoryEntityRowMapper.instance,
                id
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull List<CategoryEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, @Nonnull String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<CategoryEntity> result = jdbcTemplate.query(
                "SELECT * FROM category WHERE username = ? and name = ?",
                CategoryEntityRowMapper.instance,
                username, categoryName
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull CategoryEntity update(@Nonnull CategoryEntity category) {
        if (category.getId() == null) {
            throw new DataAccessException("При обновлении Category в CategoryEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        int updated = jdbcTemplate.update(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?",
                category.getName(),
                category.getUsername(),
                category.isArchived(),
                category.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("Категория по  id " + category.getId() + " не найдена для обновления");
        }
        return category;
    }


    @Override
    public void delete(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        int deleted = jdbcTemplate.update(
                "DELETE FROM category WHERE id = ?",
                id
        );
        if (deleted == 0) {
            throw new DataAccessException("Категория по  id " + id + " не найдена для удаления");
        }
    }
}
