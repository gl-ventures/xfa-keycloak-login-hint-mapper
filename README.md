# xfa-keycloak-login-hint-mapper
Mapping the email from the first authenticator to the XFA authenticator step in Keycloak

## Build
```
mvn clean package
```

## Install
Copy the built provider jar into `/opt/keycloak/providers`:

```
cp target/login-hint-idp-*.jar /opt/keycloak/providers
```

(the jar name carries the `<version>` from `pom.xml`, e.g.
`login-hint-idp-1.0.0.jar`)

## CI

A GitHub Actions workflow ([`.github/workflows/ci.yml`](.github/workflows/ci.yml))
runs automatically:

- **On pull requests** (targeting `main`): linting and basic validation only —
  `mvn validate` plus a compile that lints via the Java compiler
  (`-Xlint:all` with warnings promoted to errors) against the Keycloak SPIs.
- **On pushes to `main`**: the same validation, then a full build
  (`mvn clean package`) that publishes a GitHub Release tagged with the project
  version (e.g. `v1.0.0`) with the built `*.jar` attached.

The release tag follows the `<version>` from `pom.xml`. Each push to `main`
updates the release for the current version; bump the version in `pom.xml` to
cut a new, separate release.
