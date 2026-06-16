# xfa-keycloak-login-hint-mapper
Mapping the email from the first authenticator to the XFA authenticator step in Keycloak

## Build
```
mvn clean package
```

## Install
Copy `target/login-hint-idp-1.0.0.jar` into `/opt/keycloak/providers`

## CI

A GitHub Actions workflow ([`.github/workflows/ci.yml`](.github/workflows/ci.yml))
runs automatically:

- **On pull requests** (targeting `main`): linting and basic validation only —
  `mvn validate` plus a `mvn clean compile` to make sure the provider compiles
  against the Keycloak SPIs.
- **On pushes to `main`**: the same validation, then a full build
  (`mvn clean package`) that publishes a GitHub Release tagged with the project
  version (e.g. `v1.0.0`) with the built `*.jar` attached.

The release tag follows the `<version>` from `pom.xml`. Each push to `main`
updates the release for the current version; bump the version in `pom.xml` to
cut a new, separate release.
