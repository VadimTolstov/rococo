package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

/**
 * Пользовательские условия для проверки трат в таблицах интерфейса.
 * Поддерживает проверку как с учетом порядка элементов, так и без.
 */
@ParametersAreNonnullByDefault
public class SpendsConditions {

    // Форматтер для преобразования дат в строковое представление
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

    // Разделитель для объединения значений ячеек в строку
    private static final String CELL_SEPARATOR = ", ";

    /**
     * Создает условие для проверки трат с учетом порядка следования.
     *
     * @param expectedSpends массив ожидаемых объектов SpendJson
     * @return условие для проверки веб-элементов
     */
    @NotNull
    public static WebElementsCondition spends(@NotNull SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            // Строковое представление ожидаемых трат
            private final List<String> expectedSpendStrings =
                    convertSpendsToStrings(expectedSpends);

            @NotNull
            @Override
            public CheckResult check(Driver driver,  List<WebElement> elements) {
                validateSpendsArray(expectedSpends);

                // Сначала проверяем размер коллекций
                return validateSize(expectedSpends.length, elements.size(), elements)
                        .or(() -> {
                            // Преобразуем фактические элементы в строки
                            List<String> actualSpendStrings = convertElementsToStrings(elements);

                            // Поэлементное сравнение через Stream API
                            boolean allMatches = IntStream.range(0, expectedSpendStrings.size())
                                    .allMatch(i -> expectedSpendStrings.get(i).equals(actualSpendStrings.get(i)));

                            return allMatches
                                    ? Optional.of(accepted())
                                    : Optional.of(createMismatchResult(expectedSpendStrings, actualSpendStrings));
                        })
                        .get();
            }

            @NotNull
            @Override
            public String toString() {
                return "Ожидаемые траты: " + expectedSpendStrings;
            }
        };
    }

    /**
     * Создает условие для проверки трат без учета порядка следования.
     *
     * @param expectedSpends массив ожидаемых объектов SpendJson
     * @return условие для проверки веб-элементов
     */
    @NotNull
    public static WebElementsCondition spendsInAnyOrder(@NotNull SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            // Используем LinkedHashSet для сохранения порядка, но сравнения без учета порядка
            private final Set<String> expectedSpendSet =
                    new LinkedHashSet<>(convertSpendsToStrings(expectedSpends));

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                validateSpendsArray(expectedSpends);

                return validateSize(expectedSpends.length, elements.size(), elements)
                        .or(() -> {
                            // Конвертируем фактические элементы в множество
                            Set<String> actualSpendSet =
                                    new LinkedHashSet<>(convertElementsToStrings(elements));

                            // Сравнение множеств
                            return actualSpendSet.equals(expectedSpendSet)
                                    ? Optional.of(accepted())
                                    : Optional.of(createMismatchResult(
                                    new ArrayList<>(expectedSpendSet),
                                    new ArrayList<>(actualSpendSet)
                            ));
                        })
                        .get();
            }

            @NotNull
            @Override
            public String toString() {
                return "Ожидаемые траты в произвольном порядке: " + expectedSpendSet;
            }
        };
    }

    /**
     * Преобразует массив SpendJson в список строк для сравнения.
     *
     * @param spends массив объектов SpendJson
     * @return список форматированных строк
     */
    @NotNull
    private static List<String> convertSpendsToStrings(SpendJson[] spends) {
        return Arrays.stream(spends)
                .map(spend -> String.join(CELL_SEPARATOR,
                        // Форматируем каждое поле с поясняющим префиксом
                        "Category " + spend.category().name(),
                        "Amount " + spend.amount(),
                        "Description " + spend.description(),
                        "MMM dd, yyyy " + DATE_FORMAT.format(spend.spendDate())
                ))
                .toList();
    }

    /**
     * Преобразует веб-элементы в список строк для сравнения.
     *
     * @param elements список строк таблицы
     * @return список форматированных строк
     */
    @NotNull
    private static List<String> convertElementsToStrings(List<WebElement> elements) {
        return elements.stream()
                .map(element -> {
                    // Извлекаем ячейки из строки таблицы
                    List<WebElement> cells = element.findElements(By.cssSelector("td"));

                    // Форматируем значения из нужных колонок
                    return String.join(CELL_SEPARATOR,
                            "Category " + cells.get(1).getText(),
                            "Amount " + parseAmount(cells.get(2)),
                            "Description " + cells.get(3).getText(),
                            "MMM dd, yyyy " + cells.get(4).getText()
                    );
                })
                .toList();
    }

    /**
     * Парсит значение суммы из текста ячейки.
     *
     * @param amountCell веб-элемент с суммой
     * @return числовое значение суммы
     */
    private static double parseAmount(WebElement amountCell) {
        // Пример текста: "100.00 USD" → парсим первую часть
        return Double.parseDouble(amountCell.getText().split(" ")[0]);
    }

    /**
     * Проверяет соответствие размеров коллекций.
     *
     * @param expected ожидаемый размер
     * @param actual фактический размер
     * @param elements элементы для отчета об ошибке
     * @return Optional с результатом проверки, если размеры не совпадают
     */
    @NotNull
    private static Optional<CheckResult> validateSize(int expected, int actual, List<WebElement> elements) {
        if (expected != actual) {
            return Optional.of(rejected(
                    "Несоответствие размера (ожидалось: %d, фактически: %d)".formatted(expected, actual),
                    elements
            ));
        }
        return Optional.empty();
    }

    /**
     * Создает результат с описанием несоответствия.
     *
     * @param expected список ожидаемых значений
     * @param actual список фактических значений
     * @return результат проверки с детализированным сообщением
     */
    @NotNull
    private static CheckResult createMismatchResult(List<String> expected, List<String> actual) {
        return rejected(
                "Обнаружены расхождения!%nОжидалось:%n%s%nФактические значения:%n%s"
                        .formatted(String.join("%n", expected), String.join("%n", actual)),
                actual
        );
    }

    /**
     * Проверяет валидность массива трат.
     *
     * @param spends массив для проверки
     * @throws IllegalArgumentException если массив пуст
     */
    private static void validateSpendsArray(SpendJson... spends) {
        if (ArrayUtils.isEmpty(spends)) {
            throw new IllegalArgumentException("Массив ожидаемых трат не должен быть пустым");
        }
    }
}