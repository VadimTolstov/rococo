package guru.qa.rococo.jupiter.annotation;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(SpendingExtension.class)
public @interface Spending {

  String category();

  String description();

  double amount();

  CurrencyValues currency() default CurrencyValues.RUB;
}
