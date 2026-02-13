package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {


    private By loginBtn = By.cssSelector("[data-testid='login-btn']");
    private By registerBtn = By.cssSelector("[data-testid='register-btn']");

    private By loginBtnFallback = By.xpath("//button[.//text()[contains(.,'Login')]]");


    public HomePage(WebDriver driver) {
        super(driver);
    }

    public LoginPage clickLogin() {
        try {
            click(loginBtn);
        } catch (Exception e) {
            click(loginBtnFallback);
        }
        return new LoginPage(driver);
    }

    public void clickRegister() {
        click(registerBtn);
    }

    public boolean isLoaded() {
        return isVisible(loginBtn) || isVisible(loginBtnFallback);
    }
}
