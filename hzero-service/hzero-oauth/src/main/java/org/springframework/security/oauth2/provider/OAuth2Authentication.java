package org.springframework.security.oauth2.provider;

import java.security.Principal;
import java.util.Objects;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * An OAuth 2 authentication token can contain two authentications: one for the client and one for the user. Since some
 * OAuth authorization grants don't require user authentication, the user authentication may be null.
 * 
 * @author Ryan Heaton
 */
public class OAuth2Authentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -4809832298438307309L;

	private final OAuth2Request storedRequest;

	private final Authentication userAuthentication;
	private Object details;

	private final Object principal;

	/**
	 * Construct an OAuth 2 authentication. Since some grant types don't require user authentication, the user
	 * authentication may be null.
	 * 
	 * @param storedRequest The authorization request (must not be null).
	 * @param userAuthentication The user authentication (possibly null).
	 */
	public OAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication) {
		super(userAuthentication == null ? storedRequest.getAuthorities() : userAuthentication.getAuthorities());
		this.storedRequest = storedRequest;
		this.userAuthentication = userAuthentication;
		if (userAuthentication != null) {
			this.principal = userAuthentication.getPrincipal();
		} else {
			principal = null;
		}
	}

	public OAuth2Authentication(OAuth2Request storedRequest, Authentication userAuthentication, Object principal) {
		super(userAuthentication == null ? storedRequest.getAuthorities() : userAuthentication.getAuthorities());
		this.storedRequest = storedRequest;
		this.userAuthentication = userAuthentication;
		this.principal = principal;
	}

	@Override
	public Object getCredentials() {
		return "";
	}

	@Override
	public Object getPrincipal() {
		if (this.principal != null) {
			return this.principal;
		}

		return this.userAuthentication == null ? this.storedRequest.getClientId() : this.userAuthentication.getPrincipal();
	}

	/**
	 * Convenience method to check if there is a user associated with this token, or just a client application.
	 * 
	 * @return true if this token represents a client app not acting on behalf of a user
	 */
	public boolean isClientOnly() {
		return ! (this.principal instanceof UserDetails);
	}

	/**
	 * The authorization request containing details of the client application.
	 * 
	 * @return The client authentication.
	 */
	public OAuth2Request getOAuth2Request() {
		return storedRequest;
	}

	/**
	 * The user authentication.
	 * 
	 * @return The user authentication.
	 */
	public Authentication getUserAuthentication() {
		return userAuthentication;
	}

	@Override
	public boolean isAuthenticated() {
		return this.storedRequest.isApproved()
				&& (this.userAuthentication == null || this.userAuthentication.isAuthenticated());
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		if (this.userAuthentication != null && CredentialsContainer.class.isAssignableFrom(this.userAuthentication.getClass())) {
			CredentialsContainer.class.cast(this.userAuthentication).eraseCredentials();
		}
	}

	@Override
	public Object getDetails() {
		return details;
	}

	@Override
	public void setDetails(Object details) {
		this.details = details;
	}

	@Override
	public String getName() {
		if (this.getPrincipal() instanceof UserDetails) {
			return ((UserDetails) this.getPrincipal()).getUsername();
		}
		if (this.getPrincipal() instanceof ClientDetails) {
			// client 前缀避免 clientId 与用户名冲突
			return "client:" + ((ClientDetails) this.getPrincipal()).getClientId();
		}
		if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
			return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
		}
		if (this.getPrincipal() instanceof Principal) {
			return ((Principal) this.getPrincipal()).getName();
		}

		return (this.getPrincipal() == null) ? "" : this.getPrincipal().toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof OAuth2Authentication)) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		OAuth2Authentication that = (OAuth2Authentication) o;

		if (!storedRequest.equals(that.storedRequest)) {
			return false;
		}
		if (!Objects.equals(userAuthentication, that.userAuthentication)) {
			return false;
		}

		//if (getDetails()!=null ? !getDetails().equals(that.getDetails()) : that.getDetails()!=null) {
		//	// return false;
		//}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + storedRequest.hashCode();
		result = 31 * result + (userAuthentication != null ? userAuthentication.hashCode() : 0);
		return result;
	}

}
