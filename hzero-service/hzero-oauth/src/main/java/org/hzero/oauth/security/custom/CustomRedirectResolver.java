package org.hzero.oauth.security.custom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author flyleft
 */
public class CustomRedirectResolver implements RedirectResolver {


    private Collection<String> redirectGrantTypes = Arrays.asList("implicit", "authorization_code");

    private boolean matchSubdomains = true;

    private boolean matchPorts = true;

    private boolean checkRedirectUriMatch = true;

    public void setMatchSubdomains(boolean matchSubdomains) {
        this.matchSubdomains = matchSubdomains;
    }

    public void setMatchPorts(boolean matchPorts) {
        this.matchPorts = matchPorts;
    }

    public void setRedirectGrantTypes(Collection<String> redirectGrantTypes) {
        this.redirectGrantTypes = new HashSet<>(redirectGrantTypes);
    }

    public void setCheckRedirectUriMatch(boolean checkRedirectUriMatch) {
        this.checkRedirectUriMatch = checkRedirectUriMatch;
    }

    @Override
    public String resolveRedirect(String requestedRedirect, ClientDetails client) {

        Set<String> authorizedGrantTypes = client.getAuthorizedGrantTypes();
        if (authorizedGrantTypes.isEmpty()) {
            throw new InvalidGrantException("A client must have at least one authorized grant type.");
        }
        if (!containsRedirectGrantType(authorizedGrantTypes)) {
            throw new InvalidGrantException(
                    "A redirect_uri can only be used by implicit or authorization_code grant types.");
        }

        Set<String> redirectUris = client.getRegisteredRedirectUri();

        if (CollectionUtils.isEmpty(redirectUris)) {
            if (requestedRedirect == null) {
                throw new InvalidRequestException("A redirect_uri must be supplied.");
            } else {
                return requestedRedirect;
            }
        } else {
            if (checkRedirectUriMatch) {
                return obtainMatchingRedirect(redirectUris, requestedRedirect);
            } else {
                if (requestedRedirect == null) {
                    return redirectUris.iterator().next();
                } else {
                    return requestedRedirect;
                }
            }
        }
    }

    /**
     * @param grantTypes some grant types
     * @return true if the supplied grant types includes one or more of the redirect types
     */
    private boolean containsRedirectGrantType(Set<String> grantTypes) {
        for (String type : grantTypes) {
            if (redirectGrantTypes.contains(type)) {
                return true;
            }
        }
        return false;
    }

    protected boolean redirectMatches(String requestedRedirect, String redirectUri) {
        try {
            URL req = new URL(requestedRedirect);
            URL reg = new URL(redirectUri);

            int requestedPort = req.getPort() != -1 ? req.getPort() : req.getDefaultPort();
            int registeredPort = reg.getPort() != -1 ? reg.getPort() : reg.getDefaultPort();

            boolean portsMatch = !matchPorts || (registeredPort == requestedPort);

            if (reg.getProtocol().equals(req.getProtocol()) &&
                    hostMatches(reg.getHost(), req.getHost()) &&
                    portsMatch) {
                return StringUtils.cleanPath(req.getPath()).startsWith(StringUtils.cleanPath(reg.getPath()));
            }
        } catch (MalformedURLException e) {
            // do nothing
        }
        return requestedRedirect.equals(redirectUri);
    }

    protected boolean hostMatches(String registered, String requested) {
        if (matchSubdomains) {
            return registered.equals(requested) || requested.endsWith("." + registered);
        }
        return registered.equals(requested);
    }

    private String obtainMatchingRedirect(Set<String> redirectUris, String requestedRedirect) {
        Assert.notEmpty(redirectUris, "Redirect URIs cannot be empty");

        if (redirectUris.size() == 1 && requestedRedirect == null) {
            return redirectUris.iterator().next();
        }
        for (String redirectUri : redirectUris) {
            if (requestedRedirect != null && redirectMatches(requestedRedirect, redirectUri)) {
                return requestedRedirect;
            }
        }
        throw new RedirectMismatchException("Invalid redirect: " + requestedRedirect
                + " does not match one of the registered values: " + redirectUris.toString());
    }
}
