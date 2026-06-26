/*
 * Copyright 2026 XFA (https://xfa.tech)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tech.xfa.keycloak;

import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.authenticators.browser.IdentityProviderAuthenticatorFactory;
import org.keycloak.models.KeycloakSession;

public class LoginHintIdpAuthenticatorFactory extends IdentityProviderAuthenticatorFactory {
    @Override public String getId() { return "login-hint-idp-redirector"; }
    @Override public String getDisplayType() { return "IdP Redirector (login_hint)"; }
    @Override public Authenticator create(KeycloakSession s) { return new LoginHintIdpAuthenticator(); }
}
