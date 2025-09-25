package guru.qa.rococo.mapper.auth;

import guru.qa.rococo.data.entity.auth.AuthorityEntity;
import guru.qa.rococo.model.rest.auth.AuthorityJson;
import lombok.NonNull;

public class AuthorityMapper {

    /**
     * Преобразует AuthorityEntity в AuthorityJson.
     *
     * @param entity Сущность AuthorityEntity.
     * @return Объект AuthorityJson.
     */
    private  @NonNull AuthorityJson fromEntity(@NonNull AuthorityEntity entity) {
        return new AuthorityJson(
                entity.getId(),
                new AuthUserMapper().toJson(entity.getUser()),
                entity.getAuthority()
        );
    }

    /**
     * Преобразует AuthorityJson в AuthorityEntity.
     *
     * @param json Сущность AuthorityJson.
     * @return Объект AuthorityEntity.
     */
    public  @NonNull AuthorityEntity toEntity(@NonNull AuthorityJson json) {
        return new AuthorityEntity(
                json.id(),
                new AuthUserMapper().toEntity(json.userId()),
                json.authority()
        );
    }
}
