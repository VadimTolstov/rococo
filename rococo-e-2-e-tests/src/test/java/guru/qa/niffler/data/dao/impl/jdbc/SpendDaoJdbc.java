package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.CurrencyValues;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@Slf4j
public class SpendDaoJdbc implements SpendDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend(username, spend_date, currency, amount, description, category_id)" +
                        "VALUES (?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании траты", e);
        }
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {

                    SpendEntity se = new SpendEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            new java.util.Date(rs.getDate("spend_date").getTime()),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            new CategoryEntity(rs.getObject("category_id", UUID.class))
                    );
                    return Optional.ofNullable(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске траты по id = " + id, e);
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        List<SpendEntity> spendEntityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    spendEntityList.add(SpendEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return spendEntityList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при получении данных из таблицы spend", e);
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findByCategoryId(@Nonnull UUID categoryId) {
        List<SpendEntity> spendEntityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE category_id = ?"
        )) {
            ps.setObject(1, categoryId);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    spendEntityList.add(SpendEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return spendEntityList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске трат по category_id = " + categoryId, e);
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? AND description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity se = new SpendEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            new java.util.Date(rs.getDate("spend_date").getTime()),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            new CategoryEntity(rs.getObject("category_id", UUID.class))
                    );
                    return Optional.ofNullable(se);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске траты по username = " + username + " и description = " + description, e);
        }
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        if (spend.getId() == null) {
            throw new DataAccessException("При обновлении Spend в SpendEntity id не должен быть null");
        }
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?," +
                        " description = ?, category_id = ? WHERE id = ?"
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Трата с id " + spend.getId() + " не найдена");
            }

            return spend;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении траты с id {}", spend.getId(), e);
            throw new DataAccessException("Ошибка при обновлении траты", e);
        }
    }

    @Override
    public void delete(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Траты с id " + id + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении траты с id {}", id, e);
            throw new DataAccessException("Ошибка при удалении траты с id " + id, e);
        }
    }
}
