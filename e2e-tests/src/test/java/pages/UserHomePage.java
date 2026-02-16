package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class UserHomePage extends BasePage {

    private By openFavoritesBtn = By.cssSelector("[data-testid='open-favorites-btn']");
    private By addFavoritesBtn = By.cssSelector("[data-testid='add-favorites-btn']");
    private By map = By.cssSelector("[data-testid='map']");

    private By pickupInput = By.cssSelector("[data-testid='pickup-input'] input");
    private By dropoffInput = By.cssSelector("[data-testid='dropoff-input'] input");
    
    protected By snackBar = By.cssSelector(".mat-mdc-snack-bar-container");
    
    private By vehicleDropdown = By.cssSelector("[data-testid='vehicle-dropdown']");
    private By timeDropdown = By.cssSelector("[data-testid='time-dropdown']");
    private By confirmBtn = By.cssSelector("[data-testid='confirm-ride-btn']");

    private By optionStandard = By.xpath("//mat-option//span[contains(text(),'Standard')]");
    private By optionNow = By.xpath("//mat-option//span[contains(text(),'Now')]");

    private By driverMarkers = By.xpath("//img[@alt='Marker']");

    private By activityButton = By.xpath("//span[contains(translate(text(), 'HISTORY', 'history'), 'history')]");

    public UserHomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(openFavoritesBtn);
    }

    public FavoriteRouteDialog addToFavorites() {
        scrollTo(addFavoritesBtn);
        click(addFavoritesBtn);

        return new FavoriteRouteDialog(driver);
    }


    public void clickOnMap(int xOffset, int yOffset) {
        WebElement mapElement = waitForVisible(map);

        new Actions(driver)
                .moveToElement(mapElement, xOffset, yOffset)
                .click()
                .perform();
    }


    public void selectRouteOnMap() {

    	clickOnMap(-150, -120);

        wait.until(driver -> {
            String value = driver.findElement(pickupInput).getAttribute("value");
            return value != null && !value.isEmpty();
        });

        clickOnMap(-60, -60);

        wait.until(driver -> {
            String value = driver.findElement(dropoffInput).getAttribute("value");
            return value != null && !value.isEmpty();
        });
    }
    
    public boolean orderRideWithDefaults() {
    	
    	waitForDriversOnMap();

        waitForVisible(openFavoritesBtn);

        click(vehicleDropdown);
        click(optionStandard);

        click(timeDropdown);
        click(optionNow);

        scrollTo(confirmBtn);
        click(confirmBtn);

        String text = waitForSnackBarText();

        return text.contains("Ride accepted") || text.contains("rejected");
    }

    
    public boolean isFavoriteAddedSnackShown() {
        String text = waitForSnackBarText();
        return text.contains("Route added to favorites");
    }

    
    public void openFavorites() {
    	waitForSnackBarToDisappear();
    	
        scrollTo(openFavoritesBtn);
        click(openFavoritesBtn);
    }
    
    public void selectVehicleStandard() {
        click(vehicleDropdown);
        waitForVisible(optionStandard);
        click(optionStandard);
    }

    public void selectTimeNow() {
        click(timeDropdown);
        waitForVisible(optionNow);
        click(optionNow);
    }
    
    public void confirmRide() {
        scrollTo(confirmBtn);
        click(confirmBtn);
    }
    
    public boolean isRideAccepted() {
        String text = waitForSnackBarText();
        return text.contains("Ride accepted");
    }

    public boolean isRideRejected() {
        String text = waitForSnackBarText();
        return text.contains("rejected");
    }
    
    public void waitForDriversOnMap() {
        wait.until(d -> d.findElements(driverMarkers).size() >= 2);
    }

    public boolean isFavoriteSaveFailedSnackShown() {
        String text = waitForSnackBarText();
        return text.contains("Failed to save route");
    }

    public void navigateToActivity() {
        click(activityButton);
    }

}
