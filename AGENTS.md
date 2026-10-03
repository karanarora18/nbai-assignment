# Project Engineering Guidelines

## Project

SDET Assignment NBAI

## Primary Goal

Build a maintainable Java automation framework covering:

- REST API automation using REST Assured
- Mobile UI automation using Appium
- Optional GitHub Actions CI/CD

## Technology Standards

- Java 17+
- Maven
- TestNG
- REST Assured
- Appium Java Client
- Jackson
- Git
- GitHub Actions

## Engineering Principles

1. Prefer simple and maintainable designs.
2. Avoid unnecessary abstractions.
3. Keep tests independent and executable in any order.
4. Do not depend on test execution order.
5. Keep configuration externalized.
6. Never hardcode credentials.
7. Prefer reusable API clients over duplicated HTTP code.
8. Prefer POJOs for request/response models where useful.
9. Use stable mobile locators.
10. Avoid XPath unless there is no better locator.
11. Keep test data generation reusable.
12. Separate framework code from test code.
13. Keep assertions in tests unless there is a strong reason otherwise.
14. Do not introduce unnecessary third-party dependencies.
15. Generated AI code must be reviewed and understood before being retained.
16. Do not add AI/MCP dependencies to runtime test execution.
17. Every implementation must be explainable during a technical interview.



## API Principles

- Base URI must be configurable.
- Authentication should be reusable.
- API clients should encapsulate endpoint interaction.
- Request and response validation should be explicit.
- Tests should validate both HTTP behavior and business data.
- Round-trip data should be validated where applicable.
- Negative scenarios are required.
- Tests should clean up created data when appropriate.



## Mobile Principles

- Use Page Object Model.
- Driver lifecycle should be centralized.
- Capabilities/configuration should be externalized.
- Prefer accessibility/resource-id based locators.
- Keep mobile tests independent.
- Avoid excessive page-object abstraction.



## AI Usage

AI tools may accelerate development, but:

- AI-generated code must be reviewed.
- AI suggestions must be validated by executing tests.
- Final architecture decisions remain human-reviewed.
- Significant AI/MCP usage must be documented in AI_USAGE.md._

