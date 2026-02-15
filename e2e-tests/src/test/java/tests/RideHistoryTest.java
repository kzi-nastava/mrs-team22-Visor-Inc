package tests;

import base.BaseTest;
import org.junit.jupiter.api.*;
import pages.ActivityPage;
import pages.HomePage;
import pages.LoginPage;
import pages.UserHomePage;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    final ActivityPage activityPage = new ActivityPage(driver);
  }

  @Test
  @Order(2)
  @DisplayName("2. Filter by start date")
  public void rideHistoryFilterByStartDateTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user1@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
    final ActivityPage activityPage = new ActivityPage(driver);
    LocalDateTime daysAgo = LocalDateTime.now().minusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
    List<LocalDateTime> allRideDates = activityPage.rideHeaderDateTimes();
    final boolean hasOldRides = allRideDates.stream().anyMatch(date -> date.isBefore(daysAgo));
    assertTrue(hasOldRides, "Test setup error: No rides older than 2 days found to filter out!");
    activityPage.chooseFromDate(daysAgo);
    final List<LocalDateTime> filteredRideDates = activityPage.rideHeaderDateTimes();
    for (LocalDateTime date : filteredRideDates) {
      assertFalse(date.isBefore(daysAgo), "Filter failed! Found a ride from " + date + " which is older than " + daysAgo);
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    LocalDateTime daysAgo = LocalDateTime.now().minusDays(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
    List<LocalDateTime> allRideDates = activityPage.rideHeaderDateTimes();
    final boolean hasNewRides = allRideDates.stream().anyMatch(date -> date.isAfter(daysAgo));
    assertTrue(hasNewRides, "Test setup error: No rides newer than 2 days found to filter out!");
    activityPage.chooseToDate(daysAgo);
    final List<LocalDateTime> filteredRideDates = activityPage.rideHeaderDateTimes();
    for (LocalDateTime date : filteredRideDates) {
      assertFalse(date.isAfter(daysAgo), "Filter failed! Found a ride from " + date + " which is newer than " + daysAgo);
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    LocalDateTime fromDaysAgo = LocalDateTime.now().minusDays(3).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime toDaysAgo = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    List<LocalDateTime> allRideDates = activityPage.rideHeaderDateTimes();
    final boolean hasNewRides = allRideDates.stream().anyMatch(date -> date.isAfter(fromDaysAgo) && date.isBefore(toDaysAgo));
    assertTrue(hasNewRides, "Test setup error: No rides newer than 2 days found to filter out!");
    activityPage.chooseFromDate(fromDaysAgo);
    activityPage.chooseToDate(toDaysAgo);
    final List<LocalDateTime> filteredRideDates = activityPage.rideHeaderDateTimes();
    for (LocalDateTime date : filteredRideDates) {
      assertFalse(date.isAfter(toDaysAgo) || date.isBefore(fromDaysAgo), "Filter failed! Found a ride from " + date + " which is newer than " + toDaysAgo + "or older than" + fromDaysAgo);
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.chooseFromDate(LocalDateTime.of(2026, 2, 12, 0, 0));
    activityPage.chooseToDate(LocalDateTime.of(2026, 2, 7, 0, 0));
    assertEquals("No activity found yet", activityPage.noRides());
  }

  @Test
  @Order(6)
  @DisplayName("6. No ride history found")
  public void noRideHistoryFoundTest() {
    final HomePage homePage = new HomePage(driver);
    final LoginPage loginPage = homePage.clickLogin();
    loginPage.login("user4@gmail.com", "test1234");
    final UserHomePage userHomePage = new UserHomePage(driver);
    userHomePage.navigateToActivity();
    final ActivityPage activityPage = new ActivityPage(driver);
    assertEquals("No activity found yet", activityPage.noRides());
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Newest first");
    List<LocalDateTime> allRideDates = activityPage.rideHeaderDateTimes();
    assertTrue(allRideDates.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRideDates.size() - 1; i++) {
      LocalDateTime currentRide = allRideDates.get(i);
      LocalDateTime nextRide = allRideDates.get(i + 1);
      assertFalse(currentRide.isBefore(nextRide), String.format("Sorting failed at index %d! Ride at %s should be newer than or equal to ride at %s", i, currentRide, nextRide));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Oldest first");
    List<LocalDateTime> allRideDates = activityPage.rideHeaderDateTimes();
    assertTrue(allRideDates.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRideDates.size() - 1; i++) {
      LocalDateTime currentRide = allRideDates.get(i);
      LocalDateTime nextRide = allRideDates.get(i + 1);
      assertFalse(currentRide.isAfter(nextRide), String.format("Sorting failed at index %d! Ride at %s should be older than or equal to ride at %s", i, currentRide, nextRide));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Price: low → high");
    List<Double> allRidePrices = activityPage.ridePrices();
    assertTrue(allRidePrices.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRidePrices.size() - 1; i++) {
      Double currentPrice = allRidePrices.get(i);
      Double nextPrice = allRidePrices.get(i + 1);
      assertFalse(currentPrice > nextPrice, String.format("Sorting failed at index %d! Ride price at %f should be less than or equal to ride price at %f", i, currentPrice, nextPrice));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Price: high → low");
    List<Double> allRidePrices = activityPage.ridePrices();
    assertTrue(allRidePrices.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRidePrices.size() - 1; i++) {
      Double currentPrice = allRidePrices.get(i);
      Double nextPrice = allRidePrices.get(i + 1);
      assertFalse(currentPrice < nextPrice, String.format("Sorting failed at index %d! Ride price at %f should be more than or equal to ride price at %f", i, currentPrice, nextPrice));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Shortest distance");
    List<Double> allRideDistances = activityPage.ridePrices();
    assertTrue(allRideDistances.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRideDistances.size() - 1; i++) {
      Double currentDistance = allRideDistances.get(i);
      Double nextDistance = allRideDistances.get(i + 1);
      assertFalse(currentDistance > nextDistance, String.format("Sorting failed at index %d! Ride distance at %f should be less than or equal to ride price at %f", i, currentDistance, nextDistance));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Longest distance");
    List<Double> allRideDistances = activityPage.ridePrices();
    assertTrue(allRideDistances.size() > 1, "Not enough rides to verify sorting.");
    for (int i = 0; i < allRideDistances.size() - 1; i++) {
      Double currentDistance = allRideDistances.get(i);
      Double nextDistance = allRideDistances.get(i + 1);
      assertFalse(currentDistance < nextDistance, String.format("Sorting failed at index %d! Ride distance at %f should be more than or equal to ride price at %f", i, currentDistance, nextDistance));
    }
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Status ascending");
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
    final ActivityPage activityPage = new ActivityPage(driver);
    activityPage.selectSortByOption("Status descending");
  }
}
