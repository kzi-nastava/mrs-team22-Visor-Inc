package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FavoriteRoutesPage extends BasePage {

    private By title = By.xpath("//*[contains(text(),'Favorite routes')]");
    private By firstRoute = By.cssSelector("mat-expansion-panel");
    private By pickRouteBtn = By.xpath("//button[contains(.,'Pick route')]");

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
}
