package pl.transport.truck.rest.jwt;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

@Getter
public class TokenBearer extends AbstractAuthenticationToken {

    private final String value;

    public TokenBearer(String value) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.value = value;
    }

    @Override
    public Object getCredentials() {
        return value;
    }

    @Override
    public Object getPrincipal() {
        return value;
    }
}
