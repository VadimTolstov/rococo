package guru.qa.rococo.mapper.auth;

import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.model.rest.auth.AuthUserJson;
import lombok.NonNull;

import java.util.ArrayList;

public class AuthUserMapper {


    /**
     * Преобразует AuthUserJson в AuthUserEntity.
     *
     * @param json Сущность AuthUserJson.
     * @return Объект AuthUserEntity.
     */
    public @NonNull AuthUserEntity toEntity(@NonNull AuthUserJson json) {
        return new AuthUserEntity(
                json.id(),
                json.username(),
                json.password(),
                json.enabled(),
                json.accountNonExpired(),
                json.accountNonLocked(),
                json.credentialsNonExpired(),
                new ArrayList<>()
        );
    }


    /**
     * Преобразует AuthUserEntity в AuthUserJson.
     *
     * @param entity Сущность AuthUserEntity.
     * @return Объект AuthUserJson.
     */
    public @NonNull AuthUserJson toJson(@NonNull AuthUserEntity entity) {
        return new AuthUserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getEnabled(),
                entity.getAccountNonExpired(),
                entity.getAccountNonLocked(),
                entity.getCredentialsNonExpired()
        );
    }
}
