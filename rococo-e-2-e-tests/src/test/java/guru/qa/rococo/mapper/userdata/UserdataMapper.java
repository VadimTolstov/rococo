package guru.qa.rococo.mapper.userdata;

import guru.qa.rococo.data.entity.userdata.UserEntity;
import guru.qa.rococo.model.rest.userdata.UserJson;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;

public class UserdataMapper {

    /**
     * Преобразует UserEntity в UserJson.
     *
     * @param entity Сущность UserJson.
     * @return Объект UserJson.
     */
    public @NonNull UserJson toJson(@NonNull UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getAvatar() != null && entity.getAvatar().length > 0
                        ? new String(entity.getAvatar(), StandardCharsets.UTF_8)
                        : null,
            ""
        );
    }

    /**
     * Преобразует UserJson в UserEntity.
     *
     * @param json Сущность UserJson.
     * @return Объект UserEntity.
     */
    public @NonNull UserEntity toEntity(@NonNull UserJson json) {
        return new UserEntity(
                json.id(),
                json.username(),
                json.firstname(),
                json.lastname(),
                json.avatar() != null ? json.avatar().getBytes(StandardCharsets.UTF_8) : null
        );
    }
}
