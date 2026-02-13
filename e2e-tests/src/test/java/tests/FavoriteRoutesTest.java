package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import base.BaseTest;
import pages.FavoriteRouteDialog;
import pages.FavoriteRoutesPage;
import pages.HomePage;
import pages.LoginPage;
import pages.UserHomePage;

public class FavoriteRoutesTest extends BaseTest {

	@Test
	void shouldOrderRideFromFavoriteRoute() {

	    HomePage homePage = new HomePage(driver);
	    LoginPage loginPage = homePage.clickLogin();

	    loginPage.login("user1@gmail.com", "test1234");

	    UserHomePage userHomePage = new UserHomePage(driver);

	    userHomePage.selectRouteOnMap();

	    FavoriteRouteDialog dialog = userHomePage.addToFavorites();
	    dialog.saveRoute("test ruta");

	    assertTrue(userHomePage.isFavoriteAddedSnackShown());

	    userHomePage.openFavorites();

	    FavoriteRoutesPage favoritesPage = new FavoriteRoutesPage(driver);
	    assertTrue(favoritesPage.isLoaded());

	    favoritesPage.pickFirstRoute();

	    assertTrue(userHomePage.orderRideWithDefaults());
	}


}
