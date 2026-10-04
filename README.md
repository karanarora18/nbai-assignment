# NBAI SDET Assignment

Test automation for the SDET take-home assignment, in two parts: a REST API suite for
[Restful Booker](https://restful-booker.herokuapp.com) and an Android UI suite for the Sauce Labs React Native
*My Demo App*. Java 17, Maven, TestNG.

## What is covered

**API automation (REST Assured + TestNG)** - tests call the live Restful Booker service through a small `BookingClient`
and typed `Booking` models; assertions stay in the tests.

- Booking lifecycle: authentication, create, get, full update (PUT), partial update (PATCH) and delete, plus
  filtering bookings by first name, last name, check-in and check-out date.
- Negative scenarios: non-existent booking, invalid credentials, update/delete with an invalid token, update with
  missing required fields, and deleting a non-existent booking.
- Each test creates its own booking and cleans it up afterwards, so tests are independent.

**Mobile automation (Appium + TestNG, Android)** - Page Object Model on top of the UiAutomator2 driver, using the app's
accessibility ids. Each test gets a fresh app session.

- Smoke: app launches and shows the Products screen.
- Login: valid login and invalid login (error message).
- Product details: open a product, change quantity, add to cart.
- Cart: added product, quantity, item count and total.
- Checkout: end-to-end purchase of two backpacks (login if prompted, address, payment, review, order confirmation).

Mobile tests currently run against a local Appium server and emulator. iOS and cloud providers (e.g. BrowserStack) are
not implemented.

## Configuration

| Variable | Used by | Purpose |
|---|---|---|
| `RESTFUL_BOOKER_USERNAME` | API | Auth username. Required, no default. |
| `RESTFUL_BOOKER_PASSWORD` | API | Auth password. Required, no default. |
| `RESTFUL_BOOKER_BASE_URI` | API | Optional; defaults to `https://restful-booker.herokuapp.com`. |
| `mobile.app.path` / `MOBILE_APP_PATH` | Mobile | Path to the app `.apk`. Required. |
| `mobile.appium.url` / `MOBILE_APPIUM_URL` | Mobile | Appium server, default `http://127.0.0.1:4723`. |
| `mobile.device.name` / `MOBILE_DEVICE_NAME` | Mobile | Default `Android Emulator`. |
| `mobile.platform.version` / `MOBILE_PLATFORM_VERSION` | Mobile | Optional Android version. |

Mobile settings are read from a `-D` system property first, then the environment variable, then the default.
No credentials are stored in the repository.

## Running the tests

API tests (the mobile tests are excluded by default):

```
export RESTFUL_BOOKER_USERNAME=<username>
export RESTFUL_BOOKER_PASSWORD=<password>
mvn test
```

Mobile tests, locally: start an Android emulator and an Appium server with the UiAutomator2 driver, then

```
mvn test -Pmobile -Dmobile.app.path="/path/to/Android-MyDemoAppRN.1.3.0.build-244.apk"
mvn test -Pmobile -Dtest=CheckoutTest -Dmobile.app.path="..."    # a single mobile test class
```

## Test Reports

Test runs write Allure results to `target/allure-results/` (API and mobile tests alike). The results are plain
JSON/attachment files; the report itself is generated from them with the Allure CLI.

- **Mobile failures:** when a mobile test fails, `BaseMobileTest` attaches the screenshot to the Allure report. The same
  image is also kept under `target/screenshots/`.
- **API calls:** each API call is attached as a short text entry (method, URL, status, bodies). Cookies and headers are
  not recorded, and the `/auth` call is attached without request or response body, so passwords and tokens stay out of
  reports.

If the [Allure CLI](https://allurereport.org/docs/install/) is installed:

```
mvn test                       # or: mvn test -Pmobile -Dmobile.app.path=/path/to/app.apk
allure serve target/allure-results                                  # build and open a temporary report
allure generate target/allure-results --clean -o target/allure-report   # or write a static report
```

The report is an execution artifact: `target/` is ignored by Git and nothing generated is committed.

## Continuous Integration

GitHub Actions (`.github/workflows/ci.yml`) runs the API suite with `mvn test` on every push and pull request, using
Java 17 and cached Maven dependencies. The Allure results are uploaded as a build artifact.

Mobile tests are not run in CI: they need an Android emulator and an Appium server, so for now they run only in a
local/Appium environment (`mvn test -Pmobile ...`).

## AI-assisted development

This project was built with AI assistance (Claude, Cursor, ChatGPT; Appium MCP for exploring the app and discovering
locators). All generated code was reviewed and validated by running the tests. AI/MCP tooling is not required to run
the suite. Details are in [AI_USAGE.md](AI_USAGE.md).
