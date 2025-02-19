package guru.qa.rococo.model;

import guru.qa.rococo.service.EqualPasswordsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Пользовательская аннотация для валидации совпадения паролей.
 * Применяется к классам и проверяет, что два поля (например, пароль и подтверждение пароля) равны.
 */
@Target(ElementType.TYPE)  // Аннотация может быть применена только к классам, интерфейсам или перечислениям.
@Retention(RetentionPolicy.RUNTIME)  // Аннотация доступна во время выполнения программы.
@Constraint(validatedBy = {EqualPasswordsValidator.class})  // Валидация выполняется с помощью EqualPasswordsValidator.
public @interface EqualPasswords {

  /**
   * Сообщение об ошибке, которое будет выводиться, если валидация не пройдена.
   * По умолчанию: "Passwords should be equal".
   */
  String message() default "Passwords should be equal";

  /**
   * Группы валидации. По умолчанию пусто.
   */
  Class<?>[] groups() default {};

  /**
   * Дополнительная информация, которая может быть передана в процессе валидации.
   * По умолчанию пусто.
   */
  Class<? extends Payload>[] payload() default {};
}