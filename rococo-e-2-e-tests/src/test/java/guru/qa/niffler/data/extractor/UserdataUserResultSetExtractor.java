package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UdUserEntityRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class UserdataUserResultSetExtractor implements ResultSetExtractor<UserEntity> {

    public static final UserdataUserResultSetExtractor INSTANCE = new UserdataUserResultSetExtractor();

    private UserdataUserResultSetExtractor() {
    }

    @Override
    public @Nonnull UserEntity extractData(@Nonnull ResultSet rs) throws SQLException, DataAccessException {
        UserEntity user = null;
        while (rs.next()) {
            if (user == null) {
                user = UdUserEntityRowMapper.instance.mapRow(rs, 0);
            }
            UUID requesterId = (UUID) rs.getObject("requester_id");
            UUID addresseeId = (UUID) rs.getObject("addressee_id");
            if (requesterId != null) {
                FriendshipEntity friendship = new FriendshipEntity();
                friendship.setRequester(new UserEntity(requesterId));
                friendship.setAddressee(new UserEntity(addresseeId));
                friendship.setStatus(FriendshipStatus.valueOf(rs.getString("friendship_status")));
                friendship.setCreatedDate(rs.getDate("created_date"));

                if (user.getId().equals(requesterId)) {
                    if (user.getFriendshipRequests().stream().noneMatch(f ->
                            f.getRequester().getId().equals(requesterId) &&
                                    f.getAddressee().getId().equals(addresseeId))) {
                        user.getFriendshipRequests().add(friendship);
                    }
                } else if (user.getId().equals(addresseeId)) {
                    if (user.getFriendshipAddressees().stream().noneMatch(f ->
                            f.getRequester().getId().equals(requesterId) &&
                                    f.getAddressee().getId().equals(addresseeId))) {
                        user.getFriendshipAddressees().add(friendship);
                    }
                }
            }
        }
        return Objects.requireNonNull(user);
    }
}
