package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
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

public class SpendDaoSpringJdbc implements SpendDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO spend(username, spend_date, currency, amount, description, category_id)" +
                            "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;

        }, kh);

        final UUID generatedKey = getGeneratedId(kh, "id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<SpendEntity> result = jdbcTemplate.query(
                "SELECT * FROM spend WHERE id = ?",
                SpendEntityRowMapper.instance,
                id
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM spend",
                SpendEntityRowMapper.instance);
    }

    @Override
    public List<SpendEntity> findByCategoryId(UUID categoryId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM spend WHERE category_id = ?",
                SpendEntityRowMapper.instance,
                categoryId
        );
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        List<SpendEntity> result = jdbcTemplate.query(
                "SELECT * FROM spend WHERE username = ? AND description = ?",
                SpendEntityRowMapper.instance,
                username,
                description
        );
        return result.isEmpty() ? Optional.empty() : Optional.of(result.getFirst());
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        if (spend.getId() == null) {
            throw new DataAccessException("При обновлении Category в CategoryEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        int updated = jdbcTemplate.update(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?," +
                        " description = ?, category_id = ? WHERE id = ?",
                spend.getUsername(),
                new java.sql.Date(spend.getSpendDate().getTime()),
                spend.getCurrency().name(),
                spend.getAmount(),
                spend.getDescription(),
                spend.getCategory().getId(),
                spend.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("Трата с  id " + spend.getId() + " не найдена для обновления");
        }
        return spend;
    }

    @Override
    public void delete(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        int deleted = jdbcTemplate.update(
                "DELETE FROM spend WHERE id = ?",
                id
        );
        if (deleted == 0) {
            throw new DataAccessException("Трата с  id " + id + " не найдена для удаления");
        }
    }
}