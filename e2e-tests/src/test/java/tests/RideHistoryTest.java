package tests;

import base.BaseTest;
import org.junit.jupiter.api.*;
import pages.HomePage;
import pages.LoginPage;
import pages.UserHomePage;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E - Ride history - sorting and filtering")
public class RideHistoryTest extends BaseTest {

  @Test
  @Order(1)
  @DisplayName("1. Default ride history load")
  public void defaultRideHistoryLoadTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(2)
  @DisplayName("3. Filter by start date")
  public void rideHistoryFilterByStartDateTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(3)
  @DisplayName("3. Filter by end date")
  public void rideHistoryFilterByEndDateTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(4)
  @DisplayName("4. Filter by valid date range")
  public void rideHistoryFilterByValidDateRangeTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(5)
  @DisplayName("5. Filter by invalid date range")
  public void rideHistoryFilterByInvalidDateRangeTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(6)
  @DisplayName("6. No ride history found")
  public void noRideHistoryFoundTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(7)
  @DisplayName("7. Newest rides first")
  public void newestRidesFirstTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(8)
  @DisplayName("8. Oldest rides first")
  public void oldestRidesFirstTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(9)
  @DisplayName("9. Low to high price sort tests")
  public void lowToHighPriceSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(10)
  @DisplayName("10. High to low price sort tests")
  public void highToLowPriceSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(11)
  @DisplayName("11. Shortest distance ride sort test")
  public void shortestDistanceRideSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(12)
  @DisplayName("12. Longest distance ride sort test")
  public void longestDistanceRideSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(13)
  @DisplayName("13. Status ascending ride sort test")
  public void statusAscendingRideSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

  @Test
  @Order(14)
  @DisplayName("14. Status descending ride sort test")
  public void statusDescendingRideSortTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
  }

}
