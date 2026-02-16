package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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
  private final By rideExpansionPanelHeader = By.xpath("//mat-expansion-panel-header[@data-testid='ride-expansion-panel-header']");

  private final By rideDate = By.xpath(".//div[@data-testid='ride-date']");
  private final By rideTime = By.xpath(".//div[@data-testid='ride-time']");
  private final By ridePrice = By.xpath(".//div[@data-testid='ride-price']");
  private final By rideDistance = By.xpath(".//div[@data-testid='ride-distance']");
  private final By rideStatus = By.xpath(".//div[@data-testid='ride-status']");
  private final By noRides = By.xpath("//div[@data-testid='no-rides']");

  private final By rateRideButton = By.xpath(".//button[contains(text(), 'Rate ride')]");

  public ActivityPage(WebDriver driver) {
    super(driver);
  }

  public void openSortByDropdown() {
    waitForClickable(sortByDropdown);
    click(sortByDropdown);
  }

  public void selectSortByOption(String option) {
    openSortByDropdown();
    final By optionLocator = By.xpath("//mat-option[.//span[contains(normalize-space(.), '" + option + "')]]");
    waitForClickable(optionLocator);
    click(optionLocator);
    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    new Actions(driver).sendKeys(Keys.ESCAPE).perform();
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".cdk-overlay-backdrop")));
  }

  public void chooseFromDate(LocalDateTime date) {
    waitForVisible(fromDateInput);
    WebElement fromDateInputElement = driver.findElement(fromDateInput);
    WebElement button = fromDateInputElement.findElement(By.tagName("button"));
    wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    final By dateButton = By.xpath("//button[@aria-label='" + formatDate(date) + "']");
    waitForClickable(dateButton).click();
    new Actions(driver).sendKeys(Keys.ESCAPE).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).perform();
  }

  public void chooseToDate(LocalDateTime date) {
    waitForVisible(toDateInput);
    WebElement toDateInputElement = driver.findElement(toDateInput);
    WebElement button = toDateInputElement.findElement(By.tagName("button"));
    wait.until(ExpectedConditions.elementToBeClickable(button)).click();
    final By dateButton = By.xpath("//button[@aria-label='" + formatDate(date) + "']");
    waitForVisible(dateButton).click();
    new Actions(driver).sendKeys(Keys.ESCAPE).sendKeys(Keys.TAB).sendKeys(Keys.ENTER).perform();
  }

  public List<LocalDateTime> rideHeaderDates() {
    waitForAllVisible(rideExpansionPanel);

    waitForAllVisible(rideDate);
    waitForAllVisible(rideTime);

    final List<String> dateTexts = new ArrayList<>();
    final List<String> timeTexts = new ArrayList<>();

    try {
      final List<WebElement> dateElements = driver.findElements(rideDate);
      final List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement element : dateElements) {
        dateTexts.add(element.getText().trim());
      }

      for (WebElement element : timeElements) {
        timeTexts.add(element.getText().trim());
      }

    } catch (StaleElementReferenceException e) {
      dateTexts.clear();
      timeTexts.clear();

      final List<WebElement> dateElements = driver.findElements(rideDate);
      final List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement el : dateElements) {
        dateTexts.add(el.getText().trim());
      }

      for (WebElement el : timeElements) {
        timeTexts.add(el.getText().trim());
      }
    }

    final List<LocalDateTime> dateTimes = new ArrayList<>();
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    for (int i = 0; i < dateTexts.size() && i < timeTexts.size(); i++) {
      final LocalDate date = LocalDate.parse(dateTexts.get(i), dateFormatter);
      final LocalTime time = LocalTime.parse(timeTexts.get(i).substring(0, 5), timeFormatter);
      dateTimes.add(LocalDateTime.of(date, time));
    }

    return dateTimes;
  }

  public List<LocalDateTime> rideHeaderDateTimes() {
    waitForAllVisible(rideExpansionPanel);
    int expectedCount = driver.findElements(rideExpansionPanel).size();

    // Wait for the correct number of date and time elements with text
    wait.until(driver -> {
      List<WebElement> dates = driver.findElements(rideDate);
      List<WebElement> times = driver.findElements(rideTime);

      return dates.size() >= expectedCount
              && times.size() >= expectedCount
              && dates.stream().allMatch(el -> !el.getText().trim().isEmpty())
              && times.stream().allMatch(el -> !el.getText().trim().isEmpty());
    });

    final List<String> dateTexts = new ArrayList<>();
    final List<String> timeTexts = new ArrayList<>();

    try {
      final List<WebElement> dateElements = driver.findElements(rideDate);
      final List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement element : dateElements) {
        dateTexts.add(element.getText().trim());
      }

      for (WebElement element : timeElements) {
        timeTexts.add(element.getText().trim());
      }

    } catch (StaleElementReferenceException e) {
      dateTexts.clear();
      timeTexts.clear();

      final List<WebElement> dateElements = driver.findElements(rideDate);
      final List<WebElement> timeElements = driver.findElements(rideTime);

      for (WebElement el : dateElements) {
        dateTexts.add(el.getText().trim());
      }

      for (WebElement el : timeElements) {
        timeTexts.add(el.getText().trim());
      }
    }

    final List<LocalDateTime> dateTimes = new ArrayList<>();
    final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    for (int i = 0; i < dateTexts.size() && i < timeTexts.size(); i++) {
      final LocalDate date = LocalDate.parse(dateTexts.get(i), dateFormatter);
      final LocalTime time = LocalTime.parse(timeTexts.get(i).substring(0, 5), timeFormatter);
      dateTimes.add(LocalDateTime.of(date, time));
    }

    return dateTimes;
  }

  public List<Double> ridePrices() {
    waitForAllVisible(rideExpansionPanel);
    final List<String> priceTexts = new ArrayList<>();
    waitForAllVisible(ridePrice);

    try {
      final List<WebElement> priceElements = driver.findElements(ridePrice);
      for (WebElement element : priceElements) {
        priceTexts.add(element.getText().trim());
      }
    } catch (StaleElementReferenceException e) {
      priceTexts.clear();
      final List<WebElement> priceElements = driver.findElements(ridePrice);
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

    final List<String> distanceTexts = new ArrayList<>();
    waitForAllVisible(rideDistance);

    try {
      final List<WebElement> distanceElements = driver.findElements(rideDistance);
      for (WebElement element : distanceElements) {
        distanceTexts.add(element.getText().trim());
      }
    } catch (StaleElementReferenceException e) {
      distanceTexts.clear();
      final List<WebElement> distanceElements = driver.findElements(rideDistance);
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
    waitForAllVisible(rideExpansionPanelHeader);

    final int headerCount = driver.findElements(rideExpansionPanelHeader).size();
    waitForAllVisible(rideExpansionPanelHeader);
    final List<WebElement> rideHeaders = driver.findElements(rideExpansionPanelHeader);
    final JavascriptExecutor js = (JavascriptExecutor) driver;

    for (int i = 0; i < headerCount; i++) {
      try {
        if (i < rideHeaders.size()) {
          final WebElement header = rideHeaders.get(i);
          js.executeScript("arguments[0].scrollIntoView({block: 'center'});", header);
          wait.until(ExpectedConditions.elementToBeClickable(header));
          js.executeScript("arguments[0].click();", header);
          new WebDriverWait(driver, Duration.ofSeconds(1)).until(ExpectedConditions.presenceOfElementLocated(ridePrice));
        }
      } catch (StaleElementReferenceException ignored) {
      }
    }
  }

  public List<String> getAllRideStatuses() {
    waitForAllVisible(rideExpansionPanelHeader);

    final List<String> statuses = new ArrayList<>();
    final List<WebElement> panels = driver.findElements(rideExpansionPanelHeader);

    for (final WebElement panel : panels) {
      try {
        final String status = panel.findElement(rideStatus).getText();
        statuses.add(status);
      } catch (NoSuchElementException e) {
        statuses.add("");
      }
    }

    return statuses;
  }

  public String noRides() {
    waitForVisible(noRides);
    return driver.findElement(noRides).getText();
  }

  private String formatDate(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    return date.format(formatter);
  }

  public RateRideDialog clickRateRideOnFirstAvailable() {
    waitForAllVisible(rideExpansionPanelHeader);
    List<WebElement> headers = driver.findElements(rideExpansionPanelHeader);

    for (WebElement header : headers) {
      new Actions(driver).scrollToElement(header).perform();

      wait.until(ExpectedConditions.elementToBeClickable(header)).click();

      wait.until(ExpectedConditions.attributeContains(header, "class", "mat-expanded"));

      WebElement panel = header.findElement(By.xpath("./.."));

      try {
        WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(2))
                .until(ExpectedConditions.elementToBeClickable(panel.findElement(rateRideButton)));

        if (btn.isDisplayed()) {
          btn.click();
          return new RateRideDialog(driver);
        }
      } catch (TimeoutException | NoSuchElementException e) {
        header.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(header, "class", "mat-expanded")));
      }
    }
    throw new RuntimeException("No ratable rides found on the current page");
  }

  public boolean isRateRideButtonVisibleForAnyRide() {
    waitForAllVisible(rideExpansionPanelHeader);
    List<WebElement> headers = driver.findElements(rideExpansionPanelHeader);

    Actions actions = new Actions(driver);

    for (WebElement header : headers) {
      try {
        actions.scrollToElement(header).perform();

        wait.until(ExpectedConditions.elementToBeClickable(header)).click();

        wait.until(ExpectedConditions.attributeContains(header, "class", "mat-expanded"));

        WebElement panel = header.findElement(By.xpath("./.."));

        List<WebElement> buttons = panel.findElements(rateRideButton);
        if (!buttons.isEmpty() && buttons.get(0).isDisplayed()) {
          return true;
        }

        header.click();
        wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(header, "class", "mat-expanded")));

      } catch (Exception e) {
        System.out.println("Could not check header: " + e.getMessage());
      }
    }
    return false;
  }

  public boolean isAnyRideVisible() {
    try {
      return !driver.findElements(rideExpansionPanel).isEmpty();
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public boolean isRateButtonVisibleForRideAtIndex(int index) {
    waitForAllVisible(rideExpansionPanel);
    List<WebElement> panels = driver.findElements(rideExpansionPanel);

    if (index >= panels.size()) {
      throw new IndexOutOfBoundsException("Ride index " + index + " not found. Total rides: " + panels.size());
    }

    WebElement panel = panels.get(index);
    WebElement header = panel.findElement(rideExpansionPanelHeader);

    new Actions(driver).scrollToElement(header).perform();

    wait.until(ExpectedConditions.elementToBeClickable(header)).click();

    try {
      wait.until(ExpectedConditions.attributeContains(header, "class", "mat-expanded"));

      return wait.until(driver -> {
        List<WebElement> buttons = panel.findElements(By.xpath(".//button[contains(text(), 'Rate ride')]"));
        return !buttons.isEmpty() && buttons.get(0).isDisplayed();
      });

    } catch (Exception e) {
      return false;
    }
  }


}
