package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@Slf4j
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public void create(@Nonnull AuthorityEntity... users) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)")) {
            for (AuthorityEntity user : users) {
                ps.setObject(1, user.getUser().getId());
                ps.setString(2, user.getAuthority().name());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при добавлении прав пользователю", e);
        }
    }

    @Override
    public @Nonnull Optional<AuthorityEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity(
                            rs.getObject("id", UUID.class),
                            new AuthUserEntity(rs.getObject("user_id", UUID.class)),
                            Authority.valueOf(rs.getString("authority"))
                    );
                    return Optional.ofNullable(ae);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске данных в таблице authority  по id = " + id, e);
        }
    }

    @Override
    public @Nonnull List<AuthorityEntity> findByUserId(@Nonnull UUID userId) {
        List<AuthorityEntity> entityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"authority\" WHERE user_id = ?"
        )) {
            ps.setObject(1, userId);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity(
                            rs.getObject("id", UUID.class),
                            new AuthUserEntity(rs.getObject("user_id", UUID.class)),
                            Authority.valueOf(rs.getString("authority"))
                    );
                    entityList.add(ae);
                }
                return entityList;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске данных в таблице authority  по user_id = " + userId, e);
        }
    }

    @Override
    public @Nonnull List<AuthorityEntity> findAll() {
        List<AuthorityEntity> usersList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"authority\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    usersList.add(AuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return usersList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при получении данных в таблице authority", e);
        }
    }

    @Override
    public @Nonnull AuthorityEntity update(@Nonnull AuthorityEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении данных в таблице authority id не должен быть null");
        }
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"authority\" SET user_id = ?, authority = ? WHERE id = ?"
        )) {
            ps.setObject(1, user.getUser().getId());
            ps.setString(2, user.getAuthority().name());
            ps.setObject(3, user.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }

            return user;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении данных в таблице authority с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при обновлении данных в таблице authority", e);
        }
    }

    @Override
    public void delete(@Nonnull AuthorityEntity user) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"authority\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Запись в таблице authority с id " + user.getId() + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении записи с таблице authority с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при удалении траты с id " + user.getId(), e);
        }
    }
}
