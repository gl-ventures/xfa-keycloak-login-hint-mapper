package tech.xfa.keycloak;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.IdentityProviderAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;

public class LoginHintIdpAuthenticatorFactory extends IdentityProviderAuthenticatorFactory {
    @Override public String getId() { return "login-hint-idp-redirector"; }
    @Override public String getDisplayType() { return "IdP Redirector (login_hint)"; }
    @Override public Authenticator create(KeycloakSession s) { return new LoginHintIdpAuthenticator(); }
}
