package guru.qa.rococo.service;

import guru.qa.rococo.model.EqualPasswords;
import guru.qa.rococo.model.RegistrationModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Класс-валидатор для проверки совпадения паролей при регистрации.
 * Этот класс реализует интерфейс {@link ConstraintValidator} и проверяет, совпадают ли
 * пароль и подтверждение пароля в форме регистрации.
 * <p>
 * Если пароли не совпадают, валидатор добавляет сообщение об ошибке в контекст валидации.
 */
public class EqualPasswordsValidator implements ConstraintValidator<EqualPasswords, RegistrationModel> {

    /**
     * Проверяет, совпадают ли пароль и подтверждение пароля в форме регистрации.
     *
     * @param form    Объект {@link RegistrationModel}, содержащий данные формы регистрации.
     * @param context Контекст валидации, который используется для добавления сообщений об ошибках.
     * @return true, если пароли совпадают, иначе false.
     */
    @Override
    public boolean isValid(RegistrationModel form, ConstraintValidatorContext context) {
        // Проверяем, совпадают ли пароль и подтверждение пароля
        boolean isValid = form.password().equals(form.passwordSubmit());

        // Если пароли не совпадают, добавляем сообщение об ошибке в контекст валидации
        if (!isValid) {
            // Отключаем стандартное сообщение об ошибке
            context.disableDefaultConstraintViolation();
            // Добавляем кастомное сообщение об ошибке для поля "password"
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("password")
                    .addConstraintViolation();
        }

        // Возвращаем результат проверки
        return isValid;
    }
}