package guru.qa.niffler.data.dao.impl.springJdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
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

public class AuthUserDaoSpringJdbc implements AuthUserDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = getGeneratedId(kh, "id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        List<AuthUserEntity> result = jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE id = ?",
                AuthUserEntityRowMapper.instance,
                id
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findUserByName(@Nonnull String name) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        List<AuthUserEntity> result = jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE username = ?",
                AuthUserEntityRowMapper.instance,
                name
        );
        return result.isEmpty() ? Optional.empty() : Optional.ofNullable(result.getFirst());
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        return new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl())).query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }

    @Override
    public @Nonnull AuthUserEntity update(@Nonnull AuthUserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении данных в таблице user в AuthUserEntity id не должен быть null");
        }
        int updated = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl())).update(
                "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ?" +
                        "WHERE id = ?",
                user.getUsername(),
                user.getPassword(),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getId()
        );
        if (updated == 0) {
            throw new DataAccessException("Данные в таблице user по id " + user.getId() + " не найдена для обновления");
        }
        return user;
    }

    @Override
    public void delete(@Nonnull AuthUserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При удалении данных в таблице user в AuthUserEntity id не должен быть null");
        }
        int deleted = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl())).update(
                "DELETE FROM \"user\" where id = ?",
                user.getId()
        );
        if (deleted == 0) {
            throw new DataAccessException("При удалении данных в таблице user по id" + user.getId() + " не найдена для удаления");
        }
    }
}
