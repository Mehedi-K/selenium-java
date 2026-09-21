# selenium-java

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-4.x-43B02A?logo=selenium&logoColor=white)
![TestNG](https://img.shields.io/badge/Tests-TestNG-orange)
![CI](https://github.com/Mehedi-K/selenium-java/actions/workflows/ci.yml/badge.svg)

A Selenium WebDriver + Java test automation framework built with the **Page
Object Model**, using **TestNG** as the runner. It exercises
[saucedemo.com](https://www.saucedemo.com/), the well-known Selenium/QA
practice site, covering login, product sorting, the shopping cart, and the
full checkout flow.

This is a portfolio project meant to demonstrate a clean, maintainable UI
automation setup — explicit waits everywhere, no `Thread.sleep`, a proper
POM layer, data-driven tests, a failure-screenshot listener, and a working
GitHub Actions CI pipeline.

## Tech stack

- **Java 17**
- **Maven** for build/dependency management
- **Selenium WebDriver 4.x** — driver binaries are resolved automatically by
  Selenium Manager (bundled since Selenium 4.6), no WebDriverManager needed
- **TestNG** as the test runner (suites, `@DataProvider`, listeners)
- **SLF4J + Logback** for lightweight logging
- **Chrome** as the primary browser, with a headless toggle for CI

## Prerequisites

- Java 17 (JDK)
- Maven 3.9+
- Google Chrome installed locally (Selenium Manager will fetch a matching
  chromedriver automatically)

## Running the tests

Run the full suite (visible browser window):

```bash
mvn test
```

Run headless (used in CI):

```bash
mvn test -Dheadless=true
```

You can also set the `HEADLESS=true` environment variable instead of the
system property. Test results land in `target/surefire-reports/`, and any
screenshot taken on a test failure lands in `screenshots/` (gitignored).

## Project structure

```
selenium-java/
  pom.xml                                     Maven build config (Selenium, TestNG, Logback)
  src/main/java/com/automationframework/selenium/
    pages/                                    Page Object Model classes
      BasePage.java                           Shared explicit-wait helpers
      LoginPage.java                          Login form + error handling
      ProductsPage.java                       Inventory grid, sorting, add/remove to cart, cart badge
      CartPage.java                           Cart contents, remove, checkout entry point
      CheckoutStepOnePage.java                Shipping info form (name / zip)
      CheckoutStepTwoPage.java                Order overview + totals
      CheckoutCompletePage.java               Order confirmation
    utils/
      DriverFactory.java                      Creates/quits ChromeDriver, headless toggle, thread-safe
      ConfigReader.java                       Reads config.properties (base URL, waits, etc.)
  src/test/java/com/automationframework/selenium/
    tests/
      BaseTest.java                           TestNG @BeforeMethod/@AfterMethod driver lifecycle
      LoginTest.java                          Valid/invalid/locked-out/empty-field login + data-driven sweep
      SortingTest.java                        Name/price ascending & descending sort verification
      CartTest.java                           Add/remove items, badge count, cart contents
      CheckoutTest.java                       End-to-end checkout, totals verification, data-driven product sets
    listeners/
      ScreenshotListener.java                 TestNG ITestListener: screenshot on failure
  src/test/resources/
    testng.xml                                Suite definition referencing all test classes
    config.properties                         base.url, browser, wait timeouts
  .github/workflows/ci.yml                    GitHub Actions: headless run on every push/PR to main
```

## Design notes

- **Explicit waits only.** Every page object interaction goes through
  `WebDriverWait` + `ExpectedConditions` in `BasePage`; there is no
  `Thread.sleep` anywhere in the framework.
- **Data-driven tests.** `LoginTest#loginWithMultipleCredentials` and
  `CheckoutTest#checkoutTotalsAreConsistentAcrossProductSets` use TestNG
  `@DataProvider` to sweep multiple inputs through the same test logic.
- **Failure diagnostics.** `ScreenshotListener` (a TestNG `ITestListener`)
  captures a PNG screenshot on any test failure and saves it to
  `screenshots/`, which CI uploads as a build artifact alongside the
  Surefire XML/HTML reports.
- **CI.** `.github/workflows/ci.yml` runs on every push/PR to `main`: sets
  up JDK 17, caches Maven dependencies, runs `mvn -B test -Dheadless=true`,
  and uploads the Surefire reports and any failure screenshots as artifacts.

## Target site

Tests run against [https://www.saucedemo.com/](https://www.saucedemo.com/),
a public site maintained by Sauce Labs specifically for practicing Selenium
and other UI automation tools. Known test logins (password is always
`secret_sauce`):

| Username                  | Behavior                              |
|----------------------------|----------------------------------------|
| `standard_user`            | Logs in normally                       |
| `locked_out_user`          | Login blocked with an error message    |
| `problem_user`             | Logs in but has broken UI behavior     |
| `performance_glitch_user`  | Logs in, but slowly                    |
