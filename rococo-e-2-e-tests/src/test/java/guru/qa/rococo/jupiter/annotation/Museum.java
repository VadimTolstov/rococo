package guru.qa.rococo.jupiter.annotation;

import guru.qa.rococo.model.rest.museum.Country;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {
  String title() default "";
  String description() default "";
  String photo() default "";
  Country country() default Country.RUSSIA;
  String city() default "";
}
