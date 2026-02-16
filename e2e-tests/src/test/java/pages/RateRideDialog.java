package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class RateRideDialog extends BasePage {

    private final By driverStars = By.cssSelector(".driver-star");
    private final By carStars = By.cssSelector(".car-star");
    private final By commentInput = By.tagName("textarea");
    private final By submitButton = By.xpath("//button[contains(text(), 'Submit Review')]");

    private final By validationError = By.xpath("//p[contains(@class, 'rating-validation-error')]");

    public RateRideDialog(WebDriver driver) {
        super(driver);
        waitForVisible(commentInput);
    }

    public void rateDriver(int stars) {
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stars must be 1-5");
        waitForAllVisible(driverStars);
        List<WebElement> starElements = driver.findElements(driverStars);
        starElements.get(stars - 1).click();
    }

    public void rateCar(int stars) {
        if (stars < 1 || stars > 5) throw new IllegalArgumentException("Stars must be 1-5");
        waitForAllVisible(carStars);
        List<WebElement> starElements = driver.findElements(carStars);
        starElements.get(stars - 1).click();
    }

    public void enterComment(String comment) {
        type(commentInput, comment);
    }

    public void submit() {
        click(submitButton);
    }

    public boolean isValidationErrorVisible() {
        return isVisible(validationError);
    }

    public String getValidationErrorText() {
        return getText(validationError);
    }

    public boolean isClosed() {
        return waitForInvisible(commentInput);
    }
}