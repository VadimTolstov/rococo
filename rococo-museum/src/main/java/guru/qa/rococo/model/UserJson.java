//package guru.qa.rococo.model.mm;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import guru.qa.rococo.data.UserEntity;
//
//import jakarta.annotation.Nonnull;
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//public record UserJson(
//        @JsonProperty("id")
//        UUID id,
//        @JsonProperty("username")
//        String username,
//        @JsonProperty("firstname")
//        String firstname,
//        @JsonProperty("lastname")
//        String lastname,
//        @JsonProperty("avatar")
//        String avatar
//) {
//
//    /**
//     * Преобразует UserEntity в UserJson.
//     *
//     * @param entity Сущность UserEntity.
//     * @return Объект UserJson.
//     */
//    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
//        return new UserJson(
//                entity.getId(),
//                entity.getUsername(),
//                entity.getFirstname(),
//                entity.getLastname(),
//                entity.getAvatar() != null && entity.getAvatar().length > 0
//                        ? new String(entity.getAvatar(), StandardCharsets.UTF_8)
//                        : null
//        );
//    }
//
//    /**
//     * Преобразует UserJson в UserEntity.
//     *
//     * @return Объект UserEntity.
//     */
//    public @Nonnull UserEntity toEntity() {
//        UserEntity entity = new UserEntity();
//        entity.setId(this.id);
//        entity.setUsername(this.username);
//        entity.setFirstname(this.firstname);
//        entity.setLastname(this.lastname);
//        entity.setAvatar(this.avatar != null ? this.avatar.getBytes(StandardCharsets.UTF_8) : null);
//        return entity;
//    }
//}