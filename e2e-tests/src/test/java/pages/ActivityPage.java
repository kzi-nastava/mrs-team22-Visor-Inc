package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ActivityPage extends BasePage {

  private By sortByDropdown = By.xpath("//app-dropdown[@data-testid='sortBy']");
  private By fromDateInput = By.xpath("//app-value-input-date[@data-testid='fromDate']");
  private By toDateInput = By.xpath("//app-value-input-date[@data-testid='toDate']");
  private By rideHeader = By.xpath("//mat-expansion-panel-header[@data-testid='ride-header']");


  private By rideDate = By.xpath("//div[@data-testid='ride-date']");
  private By ridePrice = By.xpath("//div[@data-testid='ride-price']");
  private By rideDistance = By.xpath("//div[@data-testid='ride-distance']");
  private By rideStatus = By.xpath("//div[@data-testid='ride-status']");

  public ActivityPage(WebDriver driver) {
    super(driver);
  }

  public void openSortByDropdown() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(sortByDropdown));
    driver.findElement(sortByDropdown).click();
  }

  public void chooseFromDate(LocalDateTime date) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(fromDateInput));
    driver.findElement(fromDateInput).findElement(By.tagName("button")).click();
    driver.findElement(By.xpath("//button[@aria-label='" + formatDate(date) + "']")).click();
  }

  public void chooseToDate(LocalDateTime date) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(toDateInput));
    driver.findElement(toDateInput).findElement(By.tagName("button")).click();
    driver.findElement(By.xpath("//button[@aria-label='" + formatDate(date) + "']")).click();
  }

  public void clickOpenRideHeader() {
    click(rideHeader);
  }

  public void getRideDate() {
    driver.findElement(rideDate).getText();
  }

  public void getRidePrice() {
    driver.findElement(ridePrice).getText();
  }

  public  void getRideDistance() {
    driver.findElement(rideDistance).getText();
  }

  public void getRideStatus() {
      driver.findElement(rideStatus).getText();
  }

  private String formatDate(LocalDateTime date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
    return date.format(formatter);
  }
}
