package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FavoriteRoutesTest extends BaseTest {

    @Test
    void shouldOpenLoginAndLoginSuccessfully() {

        HomePage homePage = new HomePage(driver);
        
        assertTrue(homePage.isLoaded());

        LoginPage loginPage = homePage.clickLogin();
        
        assertTrue(loginPage.isLoaded());

        loginPage.login(
                "user1@gmail.com",
                "test1234"
        );
    }
}
