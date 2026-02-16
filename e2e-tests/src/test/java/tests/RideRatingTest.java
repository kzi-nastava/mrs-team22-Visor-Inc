package tests;

import base.BaseTest;
import org.junit.jupiter.api.*;
import pages.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RideRatingTest extends BaseTest {

    private ActivityPage activityPage;

    @Test
    @Order(1)
    public void testRateRideHappyPath() {

        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();

        rateDialog.rateDriver(5);

        rateDialog.rateCar(4);

        rateDialog.enterComment("Great ride, very smooth!");

        rateDialog.submit();

        String snackBarText = activityPage.waitForSnackBarText();
        assertTrue(snackBarText.contains("Review submitted successfully!"));

        assertTrue(rateDialog.isClosed(), "Dialog should close after successful submission");
    }

    @Test
    public void testSubmitWithoutRatingsShouldShowError() {

        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();

        rateDialog.submit();

        assertTrue(rateDialog.isValidationErrorVisible(), "Error message should be visible");
        Assertions.assertEquals(
                "Please provide ratings for both driver and vehicle before submitting your review.",
                rateDialog.getValidationErrorText()
        );

        String snackText = activityPage.waitForSnackBarText();
        assertTrue(snackText.contains("Please provide ratings"));
    }

    @Test
    public void testSubmitWithPartialRatingShouldShowError() {

        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();

        rateDialog.rateDriver(5);

        rateDialog.submit();

        assertTrue(rateDialog.isValidationErrorVisible(), "Error message should be visible if only one rating is provided");
    }

    @Test
    @Order(0)
    public void testRateButtonDisappearsImmediatelyAfterSubmission() {
        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();

        rateDialog.rateDriver(5);
        rateDialog.rateCar(5);
        rateDialog.submit();

        activityPage.waitForSnackBarToDisappear();
        assertTrue(rateDialog.isClosed());

        boolean isButtonStillThere = activityPage.isRateButtonVisibleForRideAtIndex(0);
        Assertions.assertFalse(isButtonStillThere, "Rate ride button for the submitted ride should be hidden");
    }

    @Test
    public void testRateRideWithSpecialCharactersAndLongComment() {
        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);
        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();

        String longComment = "sve top sve top sve top sve top sve top sve top sve top sve top sve top sve top sve top sve top sve top sve top";

        rateDialog.rateDriver(5);
        rateDialog.rateCar(5);
        rateDialog.enterComment(longComment);
        rateDialog.submit();

        String snackBarText = activityPage.waitForSnackBarText();
        assertTrue(snackBarText.contains("successfully"));
    }

    @Test
    public void testRateRideWithNoComment() {
        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user1@gmail.com", "test1234");
        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);
        RateRideDialog rateDialog = activityPage.clickRateRideOnFirstAvailable();


        rateDialog.rateDriver(5);
        rateDialog.rateCar(5);
        rateDialog.enterComment("");
        rateDialog.submit();

        String snackBarText = activityPage.waitForSnackBarText();
        assertTrue(snackBarText.contains("successfully"));
    }

    @Test
    public void testNoActivityFoundScenario() {

        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user5@gmail.com", "test1234");

        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        String noRidesMessage = activityPage.noRides();
        Assertions.assertEquals("No activity found yet", noRidesMessage);

        boolean arePanelsPresent = activityPage.isAnyRideVisible();
        Assertions.assertFalse(arePanelsPresent, "Ride list should be empty");
    }

    @Test
    public void testNoRatableRidesScenario() {

        final HomePage homePage = new HomePage(driver);
        final LoginPage loginPage = homePage.clickLogin();
        loginPage.login("user3@gmail.com", "test1234");

        final UserHomePage userHomePage = new UserHomePage(driver);
        userHomePage.navigateToActivity();
        activityPage = new ActivityPage(driver);

        boolean isAnyRateButtonVisible = activityPage.isRateRideButtonVisibleForAnyRide();

        Assertions.assertFalse(isAnyRateButtonVisible,
                "No Rate ride buttons should be there for not rateable rides");
    }
}