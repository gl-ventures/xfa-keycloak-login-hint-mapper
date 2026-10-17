# xfa-keycloak-login-hint-mapper

A small [Keycloak](https://www.keycloak.org/) authenticator that forwards the
**authenticated user's email** as the OpenID Connect `login_hint` parameter when
Keycloak redirects to an external Identity Provider.

It is published and maintained by **[XFA](https://xfa.tech)** to make it easy to
add XFA device verification as a post-login step in Keycloak — but it is a
generic, standalone Keycloak provider with no XFA-specific code or runtime
dependencies, so it works with any external OpenID Connect Identity Provider.

## Why this exists

By default, Keycloak does **not** pass the identity of the already-authenticated
user to an external Identity Provider it redirects to. When you use an external
IdP as an **extra authentication factor after** the user has logged in (for
example, XFA verifying device security as a post-login step), that downstream IdP
needs to know **who** the user is.

This authenticator reads the authenticated user from the Keycloak session and
sets the `login_hint` on the outgoing redirect. Because the value comes from the
**authenticated session** (`getAuthenticatedUser()`) rather than from a parameter
supplied by the client, the identity passed downstream is guaranteed to be the
user who actually authenticated.

## How it works

`LoginHintIdpAuthenticator` extends Keycloak's built-in
`IdentityProviderAuthenticator`. Before delegating to the standard redirect, it
sets the `login_hint` client note from `context.getUser().getEmail()`:

```java
UserModel user = context.getUser();
if (user != null && user.getEmail() != null) {
    context.getAuthenticationSession()
           .setClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM, user.getEmail());
}
super.authenticate(context);
```

It is registered as an authenticator with the id `login-hint-idp-redirector`
and the display name **"IdP Redirector (login_hint)"**.

## Requirements

- Keycloak **26.x** (built and tested against `26.7.2`)
- Java 17 and Maven (to build from source)

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
`login-hint-idp-1.0.1.jar`)

Then rebuild and restart Keycloak so the provider is registered:

```
/opt/keycloak/bin/kc.sh build
# then restart the running Keycloak instance
```

> Pre-built jars are also attached to each
> [GitHub Release](https://github.com/gl-ventures/xfa-keycloak-login-hint-mapper/releases).

## Configure in Keycloak

Add the **"IdP Redirector (login_hint)"** execution to the authentication flow you
use as a **Post Login Flow**, configured to point at your external Identity
Provider. It sets the `login_hint` from the authenticated user; combine it with
your IdP's "Pass login_hint" option so Keycloak forwards that hint downstream.

For the full, XFA-specific walkthrough (creating the XFA Identity Provider and
wiring the Post Login Flow), see the XFA documentation:
<https://docs.xfa.tech/docs/admin/enforcement/idp/keycloak>

## CI

A GitHub Actions workflow ([`.github/workflows/ci.yml`](.github/workflows/ci.yml))
runs automatically:

- **On pull requests** (targeting `main`): linting and basic validation only —
  `mvn validate` plus a compile that lints via the Java compiler
  (`-Xlint:all` with warnings promoted to errors) against the Keycloak SPIs.
- **On pushes to `main`**: the same validation, then a full build
  (`mvn clean package`) that publishes a GitHub Release tagged with the project
  version (e.g. `v1.0.1`) with the built `*.jar` attached.

The release tag follows the `<version>` from `pom.xml`. Each push to `main`
updates the release for the current version; bump the version in `pom.xml` to
cut a new, separate release.

## Disclaimer

This software is provided **"as is", without warranty of any kind**, express or
implied, as set out in the [Apache License 2.0](LICENSE). Use it at your own risk
and validate it in a non-production environment before deploying it to your
production Keycloak. You are responsible for your own Keycloak deployment and its
security.

This is an **independent, open-source Keycloak provider**. It is not affiliated
with, endorsed by, or sponsored by the Keycloak project or Red Hat. "Keycloak" is
a trademark of Red Hat, Inc. "XFA" is a trademark of XFA.

## Security

To report a security vulnerability, please email **<security@xfa.tech>** rather
than opening a public issue. See [SECURITY.md](SECURITY.md) for details.

## License

Licensed under the **Apache License, Version 2.0**. See [LICENSE](LICENSE) and
[NOTICE](NOTICE) for details.

## About XFA

[XFA](https://xfa.tech) makes device security simple: it verifies that the
devices accessing your applications are secure and compliant, and enforces that
as a factor in your existing identity provider — without a heavy MDM. Learn more
at <https://xfa.tech>.
