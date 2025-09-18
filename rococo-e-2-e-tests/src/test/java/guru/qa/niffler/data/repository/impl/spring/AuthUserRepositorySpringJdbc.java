package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public @Nonnull AuthUserEntity create(@Nonnull AuthUserEntity user) {
        AuthUserEntity createUser = authUserDao.create(user);
        authAuthorityDao.create(createUser.getAuthorities().toArray(new AuthorityEntity[0]));
        return createUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(@Nonnull AuthUserEntity user) {
        return authUserDao.update(user);
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id) {
        Optional<AuthUserEntity> authUser = authUserDao.findById(id);
        authUser.ifPresent(authUserEntity ->
                authUserEntity.addAuthorities(
                        authAuthorityDao
                                .findByUserId(authUserEntity.getId())
                                .toArray(new AuthorityEntity[0])
                )
        );
        return authUser;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(@Nonnull String username) {
        return authUserDao.findUserByName(username);
    }

    @Override
    public void remove(@Nonnull AuthUserEntity user) {
        for (AuthorityEntity authority : user.getAuthorities()) {
            authAuthorityDao.delete(authority);
        }
        authUserDao.delete(user);
    }
}
