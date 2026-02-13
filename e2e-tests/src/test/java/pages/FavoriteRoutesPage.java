package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FavoriteRoutesPage extends BasePage {

    private By title = By.xpath("//*[contains(text(),'Favorite routes')]");
    private By firstRoute = By.cssSelector("mat-expansion-panel");
    private By pickRouteBtn = By.xpath("//button[contains(.,'Pick route')]");
    private By removeBtn = By.xpath("//button[contains(.,'Remove')]");
    private By snackBar = By.cssSelector(".mat-mdc-snack-bar-container");

    public FavoriteRoutesPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(title);
    }

    public void pickFirstRoute() {
        click(firstRoute);
        click(pickRouteBtn);
    }
    
    public void deleteFirstRoute() {
        click(firstRoute);
        click(removeBtn);
    }

    public boolean isRouteDeletedSnackShown() {
        String text = waitForSnackBarText();
        return text.toLowerCase().contains("deleted")
                || text.toLowerCase().contains("removed");
    }

    public boolean isNoRoutesMessageShown() {
        By noRoutes = By.xpath("//*[contains(text(),'No favorite routes')]");
        return isVisible(noRoutes);
    }
}
