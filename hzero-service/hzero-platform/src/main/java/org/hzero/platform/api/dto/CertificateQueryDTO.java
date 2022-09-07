package org.hzero.platform.api.dto;

import org.hzero.platform.domain.entity.Certificate;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * CA证书查询DTO
 *
 * @author xingxingwu.hand-china.com 2019/09/29 10:49
 */
public class CertificateQueryDTO {

	@Encrypt
	private Long certificateId;
	private Long tenantId;
	private String domainName;

	public Long getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(Long certificateId) {
		this.certificateId = certificateId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
}
