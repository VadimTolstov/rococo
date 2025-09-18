package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        AuthUserEntity createUser = authUserDao.create(user);
        authAuthorityDao.create(createUser.getAuthorities().toArray(new AuthorityEntity[0]));
        return createUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(@Nonnull AuthUserEntity user) {
        for (AuthorityEntity authority : user.getAuthorities()) {
            authAuthorityDao.update(authority);
        }
        return authUserDao.update(user);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        List<AuthorityEntity> byListAe = authAuthorityDao.findByUserId(id);
        Optional<AuthUserEntity> authUser = authUserDao.findById(id);
        AuthUserEntity user;
        if (authUser.isPresent()) {
            user = authUser.get();
            user.setAuthorities(byListAe);
            return Optional.ofNullable(user);
        }
        return Optional.empty();
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
        AuthUserEntity user;
        Optional<AuthUserEntity> authUser = authUserDao.findUserByName(username);
        if (authUser.isPresent()) {
            user = authUser.get();
            user.setAuthorities(authAuthorityDao.findByUserId(user.getId()));
            return Optional.ofNullable(user);
        }
        return Optional.empty();
    }

    @Override
    public void remove(@Nonnull AuthUserEntity user) {
        for (AuthorityEntity authority : user.getAuthorities()) {
            authAuthorityDao.delete(authority);
        }
        authUserDao.delete(user);
    }
}