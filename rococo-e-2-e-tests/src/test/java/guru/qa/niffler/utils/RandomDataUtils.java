package guru.qa.niffler.utils;

import com.github.javafaker.Faker;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class RandomDataUtils {
    private static final Faker faker = new Faker();

    public static @Nonnull String randomUsername() {
        return faker.name().username();
    }

    public static @Nonnull String randomName() {
        return faker.name().name();
    }

    public static @Nonnull String randomSurname() {
        return faker.name().lastName();
    }

    public static @Nonnull String randomCategoryName() {
        return faker.address().city();
    }

    public static @Nonnull String randomSentence(int wordsCount) {
        if (wordsCount <= 0) {
            throw new IllegalArgumentException("Words count must be greater than zero");
        }
        return faker.lorem().sentence(wordsCount).trim();
    }

    public static @Nonnull String randomPassword() {
        return faker.internet().password(3, 10);
    }
}
