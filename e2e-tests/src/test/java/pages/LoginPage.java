package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private By emailInput = By.cssSelector("[data-testid='email-input'] input");
    private By passwordInput = By.cssSelector("[data-testid='password-input'] input");
    private By loginBtn = By.cssSelector("[data-testid='login-btn']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void submit() {
        click(loginBtn);
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        submit();
    }

    public boolean isLoaded() {
        return isVisible(loginBtn);
    }

    public boolean isErrorShown() {
        By error = By.xpath("//*[contains(text(),'Invalid') or contains(text(),'error')]");
        return isVisible(error);
    }
}
