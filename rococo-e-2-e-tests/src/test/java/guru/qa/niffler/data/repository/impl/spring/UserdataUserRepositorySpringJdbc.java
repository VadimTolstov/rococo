package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.data.dao.impl.springJdbc.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private final UdUserDaoSpringJdbc udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();

    @Override
    public @Nonnull UserEntity create(@Nonnull UserEntity user) {
        return udUserDaoSpringJdbc.createUser(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        return udUserDaoSpringJdbc.findById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        return udUserDaoSpringJdbc.findByUsername(username);
    }

    @Override
    public @Nonnull UserEntity update(@Nonnull UserEntity user) {
        return udUserDaoSpringJdbc.update(user);
    }

    @Override
    public void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        udUserDaoSpringJdbc.sendInvitation(requester, addressee);
    }

    @Override
    public void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        udUserDaoSpringJdbc.addFriend(requester, addressee);
    }

    @Override
    public void remove(@Nonnull UserEntity user) {
        udUserDaoSpringJdbc.delete(user);
    }
}