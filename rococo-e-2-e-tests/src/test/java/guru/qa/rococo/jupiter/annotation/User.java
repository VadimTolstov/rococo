package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.jupiter.extension.UserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface User {
  String username() default "";
  String firstname() default "";
  String lastname() default "";
  String avatar() default "";
  String password() default "";
}
