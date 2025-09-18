package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    private static final String KEY_SEPARATOR = "|#|";

    @Nonnull
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgb) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition color(Color... expectedColors) {
        return new WebElementsCondition() {

            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColors.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @NotNull
            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            // Формируем список ожидаемых ключей для сообщений об ошибках
            private final List<String> expectedKeys = Arrays.stream(expectedBubbles)
                    .map(StatConditions::toKey)
                    .toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                validateBubblesArray(expectedBubbles);

                // Обработка результата проверки размера
                return validateSize(expectedBubbles.length, elements.size(), elements)
                        .or(() -> {
                            // Логика проверки содержимого
                            List<String> actualKeys = elementsToKeys(elements);
                            for (int i = 0; i < expectedKeys.size(); i++) {
                                if (!expectedKeys.get(i).equals(actualKeys.get(i))) {
                                    return Optional.of(createMismatchResult(expectedKeys, actualKeys));
                                }
                            }
                            return Optional.of(accepted());
                        })
                        .get(); // Безопасно, так как мы всегда возвращаем значение
            }

            @NotNull
            @Override
            public String toString() {
                return "Bubbles in order: " + expectedKeys;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final Set<String> expectedKeys = Arrays.stream(expectedBubbles)
                    .map(StatConditions::toKey)
                    .collect(Collectors.toSet());

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                validateBubblesArray(expectedBubbles);

                return validateSize(expectedBubbles.length, elements.size(), elements)
                        .or(() -> {
                            Set<String> actualKeys = new HashSet<>(elementsToKeys(elements));
                            return actualKeys.equals(expectedKeys)
                                    ? Optional.of(accepted())
                                    : Optional.of(createMismatchResult(expectedKeys, actualKeys));
                        })
                        .get();
            }

            @NotNull
            @Override
            public String toString() {
                return expectedKeys.toString();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(@NotNull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final Set<String> expectedKeys = Arrays.stream(expectedBubbles)
                    .map(StatConditions::toKey)
                    .collect(Collectors.toSet());

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                validateBubblesArray(expectedBubbles);

                if (expectedBubbles.length > elements.size()) {
                    return rejected(
                            "Insufficient elements (expected: %d, actual: %d)"
                                    .formatted(expectedBubbles.length, elements.size()),
                            elements
                    );
                }

                Set<String> actualKeys = new HashSet<>(elementsToKeys(elements));
                return actualKeys.containsAll(expectedKeys)
                        ? accepted()
                        : createMismatchResult(expectedKeys, actualKeys);
            }

            @NotNull
            @Override
            public String toString() {
                return expectedKeys.toString();
            }
        };
    }

    /**
     * Преобразует объект Bubble в уникальный строковый ключ.
     * Используется разделитель "|#|" для избежания коллизий между цветом и текстом.
     */
    @Nonnull
    private static String toKey(Bubble bubble) {
        return bubble.color().rgb + KEY_SEPARATOR + bubble.text();
    }

    /**
     * Преобразует веб-элемент в строковый ключ.
     * Извлекает цвет фона и текст элемента.
     */
    @Nonnull
    private static String toKey(WebElement element) {
        return element.getCssValue("background-color") + KEY_SEPARATOR + element.getText();
    }

    /**
     * Конвертирует список веб-элементов в список ключей.
     * Использует Stream API для эффективного преобразования.
     *
     * @param elements Список веб-элементов для конвертации
     * @return Неизменяемый список ключей (Java 16+ feature)
     */
    @Nonnull
    private static List<String> elementsToKeys(List<WebElement> elements) {
        return elements.stream()
                .map(StatConditions::toKey)
                .toList();
    }

    /**
     * Проверяет соответствие размеров ожидаемой и фактической коллекций.
     *
     * @param expectedSize Ожидаемое количество элементов
     * @param actualSize   Фактическое количество элементов
     * @param elements     Список элементов для включения в сообщение об ошибке
     * @return Optional<CheckResult>
     */
    @Nonnull
    private static Optional<CheckResult> validateSize(int expectedSize, int actualSize, List<WebElement> elements) {
        if (expectedSize != actualSize) {
            return Optional.of(rejected(
                    "Size mismatch (expected: %d, actual: %d)".formatted(expectedSize, actualSize),
                    elements
            ));
        }
        return Optional.empty();
    }

    /**
     * Создает объект CheckResult с детализированным сообщением об ошибке
     */
    @Nonnull
    private static CheckResult createMismatchResult(Collection<?> expected, Collection<?> actual) {
        return rejected(
                "Mismatch!%nExpected: %s%nActual: %s".formatted(expected, actual),
                actual
        );
    }

    private static void validateBubblesArray(Bubble... bubbles) {
        if (ArrayUtils.isEmpty(bubbles)) {
            throw new IllegalArgumentException("Expected bubbles array must not be empty");
        }
    }
}
