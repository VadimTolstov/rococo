package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import guru.qa.rococo.model.rest.museum.MuseumJson;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MuseumDetailCondition extends WebElementCondition {

  private static final String IMAGE_SELECTOR = "#page-content img";
  private static final String USER_AVATAR_SELECTOR = "#shell-header figure, #shell-header img[src*='data:image']";
  private static final String EDIT_BUTTON_TEXT = "Редактировать";
  private static final String GEO_TEXT = "#page div[class='text-center']";

  private final MuseumJson museumJson;

  public MuseumDetailCondition(MuseumJson museumJson) {
    super("museum card");
    this.museumJson = museumJson;
  }

  public static MuseumDetailCondition hasPainting(MuseumJson museumJson) {
    return new MuseumDetailCondition(museumJson);
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    if (!element.isDisplayed()) {
      return CheckResult.rejected("Element is not visible", element);
    }

    // Проверяем заголовок
    final WebElement titleElement = findElementByText(element, museumJson.title());
    if (titleElement == null) {
      return CheckResult.rejected("Title element not found. Expected: '" + museumJson.title() + "'", element);
    }

    final String actualTitle = titleElement.getText().trim();
    if (!museumJson.title().equals(actualTitle)) {
      return CheckResult.rejected("Title doesn't match. Expected: '" + museumJson.title() + "', Actual: '" + actualTitle + "'", element);
    }

    // Проверяем описание
    final WebElement descriptionElement = findElementByText(element, museumJson.description());
    if (descriptionElement == null) {
      return CheckResult.rejected("Description element not found. Expected: '" + museumJson.description() + "'", element);
    }

    final String actualDescription = descriptionElement.getText().trim();
    if (!museumJson.description().equals(actualDescription)) {
      return CheckResult.rejected("Description doesn't match. Expected: '" + museumJson.description() + "', Actual: '" + actualDescription + "'", element);
    }

    // Проверяем гео
    final WebElement geoElement = findElementBySelector(element, GEO_TEXT);
    final String geo = museumJson.geo().country().name() + ", " + museumJson.geo().city();
    if (geoElement == null) {
      return CheckResult.rejected("Geo element not found. Expected: '" + geo + "'", element);
    }

    final String actualGeo = geoElement.getText().trim();
    if (!geo.equals(actualGeo)) {
      return CheckResult.rejected("Description doesn't match. Expected: '" + geo + "', Actual: '" + actualGeo + "'", element);
    }

    // Проверяем картинку
    final WebElement img = findElementBySelector(element, IMAGE_SELECTOR);
    if (img == null || !img.isDisplayed()) {
      return CheckResult.rejected("Image not found or not visible", element);
    }

    // проверка авторизации
    boolean isUserAuthorized = isUserAvatarVisible(driver);

    // Проверяем кнопку редактирования
    final WebElement editButton = findElementByText(element, EDIT_BUTTON_TEXT);
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
      final WebElement element = driver.getWebDriver().findElement(By.cssSelector(cssSelector));
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
    return String.format("museum card with title '%s', description '%s', geo '%s'",
        museumJson.title(), museumJson.description(), museumJson.geo().country().name() + ", " + museumJson.geo().city());
  }
}