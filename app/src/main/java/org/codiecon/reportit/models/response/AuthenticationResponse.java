package org.codiecon.reportit.models.response;

import org.codiecon.reportit.models.UserPrincipal;

public class AuthenticationResponse {

    private UserPrincipal principal;

    public UserPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(UserPrincipal principal) {
        this.principal = principal;
    }

}
