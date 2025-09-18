package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.ex.DataAccessException;
import guru.qa.niffler.model.Authority;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserResultSetExtractor implements ResultSetExtractor<Optional<AuthUserEntity>> {

    public static final AuthUserResultSetExtractor INSTANCE = new AuthUserResultSetExtractor();

    private AuthUserResultSetExtractor() {
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> extractData(@Nonnull ResultSet rs) throws SQLException {
        Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);
            AuthUserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    return new AuthUserEntity(
                            id,
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getBoolean("enabled"),
                            rs.getBoolean("account_non_expired"),
                            rs.getBoolean("account_non_locked"),
                            rs.getBoolean("credentials_non_expired"),
                            new ArrayList<>()
                    );
                } catch (SQLException e) {
                    throw new DataAccessException("Error mapping user", e);
                }
            });

            // Добавление прав, если они есть
            UUID authorityId = rs.getObject("authority_id", UUID.class);
            if (authorityId != null) {
                user.getAuthorities().add(new AuthorityEntity(
                        authorityId,
                        user,
                        Authority.valueOf(rs.getString("authority"))
                ));
            }
        }
        return userMap.isEmpty() ? Optional.empty() : Optional.ofNullable(userMap.values().iterator().next());
    }
}