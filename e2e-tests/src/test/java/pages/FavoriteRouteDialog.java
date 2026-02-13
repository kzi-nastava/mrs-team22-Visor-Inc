package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FavoriteRouteDialog extends BasePage {

    private By input = By.cssSelector("[data-testid='route-name-input']");
    private By saveBtn = By.cssSelector("[data-testid='save-route-name-btn']");
    private By cancelBtn = By.cssSelector("[data-testid='cancel-route-name-btn']");

    public FavoriteRouteDialog(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isVisible(input);
    }

    public void enterName(String name) {
        type(input, name);
    }

    public void save() {
        click(saveBtn);
    }

    public void cancel() {
        click(cancelBtn);
    }

    public void saveRoute(String name) {
        enterName(name);
        click(saveBtn);

        waitForInvisible(input);
    }
}
