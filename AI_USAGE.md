# AI-Assisted Development

This project uses AI-assisted development to improve implementation speed,
exploration, code generation, review, and debugging.

## Tools

- Claude Max
- Cursor
- ChatGPT
- Appium MCP

## Intended Usage

AI tools are used for:
- framework scaffolding
- code generation
- refactoring suggestions
- test-case generation
- debugging assistance
- mobile application exploration
- locator discovery
- documentation assistance

## Engineering Principle

AI-generated code is reviewed, modified where necessary, and validated through
actual test execution before being considered part of the framework.

AI/MCP tooling is a development-time accelerator and is not required for
deterministic test execution.

## MCP Usage

Appium MCP may be used during mobile application exploration, locator
discovery, and initial test scaffolding.

The final mobile automation suite remains Java/TestNG/Appium based.

## Human Review

Architecture, design decisions, framework structure, assertions, test coverage,
and final implementation remain subject to human review.