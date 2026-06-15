# xfa-keycloak-login-hint-mapper
Mapping the email from the first authenticator to the XFA authenticator step in Keycloak

## Build
```mvn clean package```

## Install
Copy `target/login-hint-idp-1.0.0.jar` into `/opt/keycloak/providers`
