package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.CurrencyValues;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@Slf4j
public class UdUserDaoJdbc implements UdUserDao {
    private final static Config CFG = Config.getInstance();

    @Override
    public @Nonnull UserEntity createUser(@Nonnull UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)"
                , Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can't find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при создании пользователя", e);
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT DISTINCT " +
                        " u.*," +
                        " f.requester_id AS requester_id," +
                        " f.addressee_id AS addressee_id," +
                        " f.status AS friendship_status," +
                        " f.created_date AS created_date" +
                        " FROM \"user\" u LEFT JOIN \"friendship\" f" +
                        " ON u.id = f.requester_id OR u.id = f.addressee_id " +
                        " WHERE  u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            List<FriendshipEntity> feRequesterList = new ArrayList<>();
            List<FriendshipEntity> feAddresseeList = new ArrayList<>();
            UserEntity user = null;
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    if (user == null) {
                        user = UdUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    FriendshipEntity fe = new FriendshipEntity();
                    fe.setRequester(new UserEntity(rs.getObject("requester_id", UUID.class)));
                    fe.setAddressee(new UserEntity(rs.getObject("addressee_id", UUID.class)));
                    fe.setStatus(FriendshipStatus.valueOf(rs.getString("friendship_status")));
                    fe.setCreatedDate(rs.getDate("created_date"));

                    if (Objects.equals(fe.getRequester().getId(), user.getId())) {
                        feRequesterList.add(fe);
                    } else if (Objects.equals(fe.getAddressee().getId(), user.getId())) {
                        feAddresseeList.add(fe);
                    }
                }
                if (user == null) {
                    return Optional.empty();
                }
                user.setFriendshipAddressees(feAddresseeList);
                user.setFriendshipRequests(feRequesterList);
                return Optional.ofNullable(user);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по id = " + id, e);
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity(
                            rs.getObject("id", UUID.class),
                            rs.getString("username"),
                            CurrencyValues.valueOf(rs.getString("currency")),
                            rs.getString("firstname"),
                            rs.getString("surname"),
                            rs.getString("full_name"),
                            rs.getBytes("photo"),
                            rs.getBytes("photo_small"),
                            new ArrayList<>(),
                            new ArrayList<>()
                    );
                    return Optional.ofNullable(ue);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при поиске пользователя по username = " + username, e);
        }
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        List<UserEntity> userEntityList = new ArrayList<>();
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    userEntityList.add(UdUserEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return userEntityList;
        } catch (SQLException e) {
            throw new DataAccessException("Ошибка при получении данных с таблицы user ", e);
        }
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        if (user.getId() == null) {
            throw new DataAccessException("При обновлении User в UserEntity id не должен быть null");
        }
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, " +
                        "surname = ?, photo = ?, photo_small = ?, full_name = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            ps.setObject(8, user.getId());

            // Проверяем количество обновленных строк
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }
            return user;
        } catch (SQLException e) {
            log.error("Ошибка при обновлении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при обновлении пользователя", e);
        }
    }

    public void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        extractedFriend(requester, addressee, FriendshipStatus.PENDING.name());
    }


    public void addFriend(UserEntity requester, UserEntity addressee) {
        extractedFriend(requester, addressee, FriendshipStatus.ACCEPTED.name());
        extractedFriend(addressee, requester, FriendshipStatus.ACCEPTED.name());
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        try (PreparedStatement friendsPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?");
             PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?"
             )) {
            friendsPs.setObject(1, user.getId());
            friendsPs.setObject(2, user.getId());
            friendsPs.executeUpdate();

            userPs.setObject(1, user.getId());
            // Проверяем количество обновленных строк
            int affectedRows = userPs.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Пользователь с id " + user.getId() + " не найдена");
            }
        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя с id {}", user.getId(), e);
            throw new DataAccessException("Ошибка при удалении пользователя с id " + user.getId(), e);
        }
    }

    private void extractedFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee, @Nonnull String friendshipStatus) {
        if (requester.getId() == null || addressee.getId() == null) {
            throw new DataAccessException("При добавлении дружбы id не должен быть null ");
        }
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date)" +
                        "VALUES (?,?,?,?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, friendshipStatus);
            ps.setObject(4, new java.sql.Date(System.currentTimeMillis()));
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("В таблицу friendship данные не добавлены");
            }
        } catch (SQLException e) {
            throw new RuntimeException("При добавлении данных в таблицу friendship произошла ошибка " + e);
        }
    }
}
