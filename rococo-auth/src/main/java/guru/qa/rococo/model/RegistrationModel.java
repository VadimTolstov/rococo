package guru.qa.rococo.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Модель данных для регистрации пользователя.
 * Содержит поля для имени пользователя, пароля и подтверждения пароля.
 * Включает аннотации для валидации данных.
 */
@EqualPasswords
public record RegistrationModel(
    @NotBlank(message = "Username can not be blank")
    @Size(min = 3, max = 50, message = "Allowed username length should be from 3 to 50 characters")
    String username,
    @NotBlank(message = "Password can not be blank")
    @Size(min = 3, max = 12, message = "Allowed password length should be from 3 to 12 characters")
    String password,
    @NotBlank(message = "Password submit can not be blank")
    @Size(min = 3, max = 12, message = "Allowed password length should be from 3 to 12 characters")
    String passwordSubmit) {

}
