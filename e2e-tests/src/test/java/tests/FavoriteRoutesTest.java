package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import base.BaseTest;
import pages.FavoriteRouteDialog;
import pages.FavoriteRoutesPage;
import pages.HomePage;
import pages.LoginPage;
import pages.UserHomePage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E - Favorite routes feature")
public class FavoriteRoutesTest extends BaseTest {

    @Test
    @Order(1)
    @DisplayName("1. Should order ride from favorite route (happy path)")
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

    @Test
    @Order(2)
    @DisplayName("2. Should show error when adding duplicate favorite route")
    void shouldShowErrorWhenAddingDuplicateFavoriteRoute() {

        HomePage homePage = new HomePage(driver);
        LoginPage loginPage = homePage.clickLogin();

        loginPage.login("user2@gmail.com", "test1234");

        UserHomePage userHomePage = new UserHomePage(driver);

        userHomePage.selectRouteOnMap();

        FavoriteRouteDialog dialog1 = userHomePage.addToFavorites();
        dialog1.saveRoute("test ruta");

        assertTrue(userHomePage.isFavoriteAddedSnackShown());

        userHomePage.waitForSnackBarToDisappear();

        FavoriteRouteDialog dialog2 = userHomePage.addToFavorites();
        dialog2.saveRoute("test ruta");

        assertTrue(userHomePage.isFavoriteSaveFailedSnackShown());
    }
}
