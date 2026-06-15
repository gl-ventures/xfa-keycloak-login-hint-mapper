package tech.xfa.keycloak;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.authenticators.browser.IdentityProviderAuthenticator;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;

public class LoginHintIdpAuthenticator extends IdentityProviderAuthenticator {
    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();
        if (user != null && user.getEmail() != null) {
            context.getAuthenticationSession()
                   .setClientNote(OIDCLoginProtocol.LOGIN_HINT_PARAM, user.getEmail());
        }
        super.authenticate(context);
    }
}
