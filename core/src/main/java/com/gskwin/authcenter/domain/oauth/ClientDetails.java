package com.gskwin.authcenter.domain.oauth;

import java.util.Date;
import org.apache.oltu.oauth2.common.domain.client.BasicClientInfo;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import com.gskwin.authcenter.infrastructure.DateUtils;

public class ClientDetails extends BasicClientInfo {
	private String clientId;
	private String clientSecret;
	private String clientName;
	private String clientUrl;
	private String clientIconUri;
	private String resourcesIds;
	private String redirectUri;
	private String description;
	private String resourceIds;
	private String scope;
	private String grantTypes;
	private String roles; // roles
	private Integer accessTokenValidity;
	private Integer refreshTokenValidity;
	private boolean trusted = false;
	private boolean archived = false;
	private Date createTime = DateUtils.now();

	public ClientDetails() {
	}

	public String resourceIds() {
		return resourceIds;
	}

	public ClientDetails resourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
		return this;
	}

	public String scope() {
		return scope;
	}

	public ClientDetails scope(String scope) {
		this.scope = scope;
		return this;
	}

	public String grantTypes() {
		return grantTypes;
	}

	public ClientDetails grantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
		return this;
	}

	public String roles() {
		return roles;
	}

	public ClientDetails roles(String roles) {
		this.roles = roles;
		return this;
	}

	public Integer accessTokenValidity() {
		return accessTokenValidity;
	}

	public ClientDetails accessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
		return this;
	}

	public Integer refreshTokenValidity() {
		return refreshTokenValidity;
	}

	public ClientDetails refreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
		return this;
	}

	public boolean trusted() {
		return trusted;
	}

	public ClientDetails trusted(boolean trusted) {
		this.trusted = trusted;
		return this;
	}

	public boolean archived() {
		return archived;
	}

	public ClientDetails archived(boolean archived) {
		this.archived = archived;
		return this;
	}

	public Date createTime() {
		return createTime;
	}

	public ClientDetails createTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public boolean supportRefreshToken() {
		return this.grantTypes != null && this.grantTypes.contains(GrantType.REFRESH_TOKEN.toString());
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientUrl() {
		return clientUrl;
	}

	public void setClientUrl(String clientUrl) {
		this.clientUrl = clientUrl;
	}

	public String getClientIconUri() {
		return clientIconUri;
	}

	public void setClientIconUri(String clientIconUri) {
		this.clientIconUri = clientIconUri;
	}

	public String getResourcesIds() {
		return resourcesIds;
	}

	public void setResourcesIds(String resourcesIds) {
		this.resourcesIds = resourcesIds;
	}

	public String getRedirectUri() {
		return redirectUri;
	}

	public void setRedirectUri(String redirectUri) {
		this.redirectUri = redirectUri;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(String resourceIds) {
		this.resourceIds = resourceIds;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getGrantTypes() {
		return grantTypes;
	}

	public void setGrantTypes(String grantTypes) {
		this.grantTypes = grantTypes;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	public Integer getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public void setAccessTokenValidity(Integer accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	public Integer getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public void setRefreshTokenValidity(Integer refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
