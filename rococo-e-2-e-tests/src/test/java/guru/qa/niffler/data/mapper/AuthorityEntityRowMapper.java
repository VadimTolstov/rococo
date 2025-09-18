package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.Authority;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

    private AuthorityEntityRowMapper() {
    }

    @Override
    public @Nonnull AuthorityEntity mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        return new AuthorityEntity(
                rs.getObject("id", UUID.class),
                new AuthUserEntity(rs.getObject("user_id", UUID.class)),
                Authority.valueOf(rs.getString("authority"))
        );
    }
}
