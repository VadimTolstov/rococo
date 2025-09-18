package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.ex.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public void create(@Nonnull AuthorityEntity... authority) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUser().getId());
                        ps.setString(2, authority[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public @Nonnull Optional<AuthorityEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        List<AuthorityEntity> result = jdbcTemplate.query(
                "SELECT * FROM \"authority\" WHERE id = ?",
                AuthorityEntityRowMapper.instance,
                id
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\"",
                AuthorityEntityRowMapper.instance
        );
    }

    @Override
    public @Nonnull List<AuthorityEntity> findByUserId(@Nonnull UUID userId) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
                "SELECT * FROM \"authority\" WHERE user_id = ?",
                AuthorityEntityRowMapper.instance,
                userId
        );
    }


    @Override
    public @Nonnull AuthorityEntity update(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении данных в таблице authority в AuthorityEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        int updated = jdbcTemplate.update(
                "UPDATE \"authority\" SET user_id = ?, authority = ? WHERE id = ?",
                user.getUser().getId(),
                user.getAuthority().name(),
                user.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("В таблице authority данные  по  id " + user.getId() + " не найдена для обновления");
        }
        return user;
    }

    @Override
    public void delete(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При удалении данных в таблице authority в AuthorityEntity id не должен быть null");
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        int deleted = jdbcTemplate.update(
                "DELETE FROM \"authority\" WHERE id = ?",
                user.getId()
        );
        if (deleted == 0) {
            throw new DataAccessException("В таблице authority данные по id " + user.getId() + " не найдена для удаления");
        }
    }
}