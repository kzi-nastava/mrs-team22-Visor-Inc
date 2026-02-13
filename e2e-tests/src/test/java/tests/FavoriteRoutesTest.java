package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import base.BaseTest;
import pages.FavoriteRouteDialog;
import pages.HomePage;
import pages.LoginPage;
import pages.UserHomePage;

public class FavoriteRoutesTest extends BaseTest {

	@Test
	void shouldAddRouteToFavorites() {

	    HomePage homePage = new HomePage(driver);
	    LoginPage loginPage = homePage.clickLogin();

	    loginPage.login("user1@gmail.com", "test1234");

	    UserHomePage userHomePage = new UserHomePage(driver);

	    assertTrue(userHomePage.isLoaded());

	    userHomePage.selectRouteOnMap();

	    FavoriteRouteDialog dialog = userHomePage.addToFavorites();

	    assertTrue(dialog.isLoaded());

	    dialog.saveRoute("test ruta");
	}

}
