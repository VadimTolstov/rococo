package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.UdUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private final UdUserDao udUserDao = new UdUserDaoJdbc();

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        return udUserDao.createUser(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        return udUserDao.findById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        return udUserDao.findByUsername(username);
    }

    @Override
    public @Nonnull UserEntity update(UserEntity user) {
        return udUserDao.update(user);
    }

    @Override
    public void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        udUserDao.sendInvitation(requester, addressee);
    }


    @Override
    public void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        udUserDao.addFriend(requester, addressee);
    }

    @Override
    public void remove(@Nonnull UserEntity user) {
        udUserDao.delete(user);
    }
}
