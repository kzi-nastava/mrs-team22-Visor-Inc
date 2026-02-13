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
}
