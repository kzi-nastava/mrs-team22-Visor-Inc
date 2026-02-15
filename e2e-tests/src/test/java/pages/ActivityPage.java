package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ActivityPage extends BasePage {

  private final By sortByDropdown = By.xpath("//app-dropdown[@data-testid='sortBy']");
  private final By fromDateInput = By.xpath("//app-value-input-date[@data-testid='fromDate']");
  private final By toDateInput = By.xpath("//app-value-input-date[@data-testid='toDate']");
  private final By rideExpansionPanel = By.xpath("//mat-expansion-panel[@data-testid='ride-expansion-panel']");

  private final By rideDate = By.xpath(".//div[@data-testid='ride-date']");
  private final By rideTime = By.xpath(".//div[@data-testid='ride-time']");
  private final By ridePrice = By.xpath(".//div[@data-testid='ride-price']");
  private final By rideDistance = By.xpath(".//div[@data-testid='ride-distance']");
  private final By rideStatus = By.xpath(".//div[@data-testid='ride-status']");
  private final By noRides = By.xpath("//div[@data-testid='no-rides']");

  public ActivityPage(WebDriver driver) {
    super(driver);
  }

  public void openSortByDropdown() {
    waitForClickable(sortByDropdown);
    click(sortByDropdown);
  }

  public void selectSortByOption(String option) {
    openSortByDropdown();
    By optionLocator = By.xpath("//mat-option[.//span[contains(normalize-space(.), '" + option + "')]]");
    waitForClickable(optionLocator);
    click(optionLocator);
    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
  }

  public void chooseFromDate(LocalDateTime date) {
    waitForClickable(fromDateInput);
    driver.findElement(fromDateInput).findElement(By.tagName("button")).click();
    driver.findElement(By.xpath("//button[@aria-label='" + formatDate(date) + "']")).click();
    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
  }

  public void chooseToDate(LocalDateTime date) {
    waitForClickable(toDateInput);
    driver.findElement(toDateInput).findElement(By.tagName("button")).click();
    driver.findElement(By.xpath("//button[@aria-label='" + formatDate(date) + "']")).click();
    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
  }

  public List<LocalDateTime> rideHeaderDateTimes() {
    waitForAllVisible(rideExpansionPanel);

    List<String> dateTexts = new ArrayList<>();
    List<String> timeTexts = new ArrayList<>();

    try {
      List<WebElement> dateElements = driver.findElements(rideDate);
      List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement element : dateElements) {
        dateTexts.add(element.getText().trim());
      }

      for (WebElement element : timeElements) {
        timeTexts.add(element.getText().trim());
      }

    } catch (StaleElementReferenceException e) {
      dateTexts.clear();
      timeTexts.clear();

      List<WebElement> dateElements = driver.findElements(rideDate);
      List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement el : dateElements) {
        dateTexts.add(el.getText().trim());
      }

      for (WebElement el : timeElements) {
        timeTexts.add(el.getText().trim());
      }
    }

    List<LocalDateTime> dateTimes = new ArrayList<>();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    for (int i = 0; i < dateTexts.size() && i < timeTexts.size(); i++) {
      LocalDate date = LocalDate.parse(dateTexts.get(i), dateFormatter);
      LocalTime time = LocalTime.parse(timeTexts.get(i).substring(0, 5), timeFormatter);
      dateTimes.add(LocalDateTime.of(date, time));
    }

    return dateTimes;
  }

  public List<Double> ridePrices() {
    waitForAllVisible(rideExpansionPanel);
    List<String> priceTexts = new ArrayList<>();

    try {
      List<WebElement> priceElements = driver.findElements(ridePrice);
      for (WebElement element : priceElements) {
        priceTexts.add(element.getText().trim());
      }
    } catch (StaleElementReferenceException e) {
      priceTexts.clear();
      List<WebElement> priceElements = driver.findElements(ridePrice);
      for (WebElement el : priceElements) {
        priceTexts.add(el.getText().trim());
      }
    }

    return priceTexts.stream()
            .map(text -> {
              String cleanPrice = text.replaceAll("[^\\d.,]", "").replace(",", ".");
              return Double.parseDouble(cleanPrice);
            })
            .toList();
  }

  public List<Double> rideDistances() {
    waitForAllVisible(rideExpansionPanel);

    List<String> distanceTexts = new ArrayList<>();

    try {
      List<WebElement> distanceElements = driver.findElements(rideDistance);
      for (WebElement element : distanceElements) {
        distanceTexts.add(element.getText().trim());
      }
    } catch (StaleElementReferenceException e) {
      distanceTexts.clear();
      List<WebElement> distanceElements = driver.findElements(rideDistance);
      for (WebElement el : distanceElements) {
        distanceTexts.add(el.getText().trim());
      }
    }

    return distanceTexts.stream()
            .map(text -> {
              String cleanDistance = text.replaceAll("[^\\d.]", "");
              return cleanDistance.isEmpty() ? 0.0 : Double.parseDouble(cleanDistance);
            })
            .toList();
  }

  public void clickOpenRideHeader() {
    waitForVisible(rideExpansionPanel);
    final List<WebElement> rideHeaders = driver.findElements(rideExpansionPanel);
    Actions actions = new Actions(driver);
    for (WebElement header : rideHeaders) {
      actions.moveToElement(header).click().perform();
    }
  }

  public void getRideStatus(WebElement parent) {
    waitForVisible(rideStatus);
    parent.findElement(rideStatus).getText();
  }

  public String noRides() {
    waitForVisible(noRides);
    return driver.findElement(noRides).getText();
  }

  private String formatDate(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    return date.format(formatter);
  }
}
