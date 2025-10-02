package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.rococo.model.rest.painting.PaintingJson;
import lombok.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PaintingDetailCondition extends WebElementCondition {

  private static final String IMAGE_SELECTOR = "img[src*='data:image'], img.my-4.mx-auto.w-full";
  private static final String USER_AVATAR_SELECTOR = "#shell-header figure, #shell-header img[src*='data:image']";
  private static final String EDIT_BUTTON_TEXT = "Редактировать";
  private static final String LOGIN_BUTTON_TEXT = "Войти";

  private final PaintingJson painting;

  public PaintingDetailCondition(PaintingJson painting) {
    super("painting card");
    this.painting = painting;
  }

  public static PaintingDetailCondition hasPainting(PaintingJson painting) {
    return new PaintingDetailCondition(painting);
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    if (!element.isDisplayed()) {
      return CheckResult.rejected("Element is not visible", element);
    }

    // Проверяем заголовок
    WebElement titleElement = findElementByText(element, painting.title());
    if (titleElement == null) {
      return CheckResult.rejected("Title element not found. Expected: '" + painting.title() + "'", element);
    }

    String actualTitle = titleElement.getText().trim();
    if (!painting.title().equals(actualTitle)) {
      return CheckResult.rejected("Title doesn't match. Expected: '" + painting.title() + "', Actual: '" + actualTitle + "'", element);
    }

    // Проверяем описание
    WebElement descriptionElement = findElementByText(element, painting.description());
    if (descriptionElement == null) {
      return CheckResult.rejected("Description element not found. Expected: '" + painting.description() + "'", element);
    }

    String actualDescription = descriptionElement.getText().trim();
    if (!painting.description().equals(actualDescription)) {
      return CheckResult.rejected("Description doesn't match. Expected: '" + painting.description() + "', Actual: '" + actualDescription + "'", element);
    }

    // Проверяем картинку
    WebElement img = findElementBySelector(element, IMAGE_SELECTOR);
    if (img == null || !img.isDisplayed()) {
      return CheckResult.rejected("Image not found or not visible", element);
    }

    // проверка авторизации
    boolean isUserAuthorized = isUserAvatarVisible(driver);

    // Проверяем кнопку редактирования
    WebElement editButton = findElementByText(element, EDIT_BUTTON_TEXT);
    boolean isEditButtonVisible = editButton != null && editButton.isDisplayed();

    // Логика проверки кнопки редактирования
    if (!isUserAuthorized && isEditButtonVisible) {
      return CheckResult.rejected("Edit button should not be visible when user is not authorized", element);
    }

    if (isUserAuthorized && !isEditButtonVisible) {
      return CheckResult.rejected("Edit button should be visible but it's not", element);
    }

    return CheckResult.accepted();
  }

  private boolean isUserAvatarVisible(Driver driver) {
    //есть ли аватар пользователя
    return isElementVisibleInDocument(driver, USER_AVATAR_SELECTOR);
  }

  private boolean isElementVisibleInDocument(Driver driver, String cssSelector) {
    try {
      WebElement element = driver.getWebDriver().findElement(By.cssSelector(cssSelector));
      return element != null && element.isDisplayed();
    } catch (org.openqa.selenium.NoSuchElementException e) {
      return false;
    }
  }

  private WebElement findElementBySelector(WebElement context, String cssSelector) {
    try {
      return context.findElement(By.cssSelector(cssSelector));
    } catch (org.openqa.selenium.NoSuchElementException e) {
      return null;
    }
  }

  private WebElement findElementByText(WebElement context, String text) {
    try {
      return context.findElement(By.xpath(".//*[normalize-space(text())='" + text + "']"));
    } catch (org.openqa.selenium.NoSuchElementException e) {
      return null;
    }
  }

  @Nonnull
  @Override
  public String toString() {
    return String.format("painting card with title '%s', description '%s'",
        painting.title(), painting.description());
  }
}