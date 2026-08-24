package org.hzero.iam.domain.service.secgrp.enums;

/**
 * 安全组权限回收类型
 *
 * @author xingxingwu.hand-china.com 2019/12/09 11:31
 */
public enum SecGrpAuthorityRevokeType {
	REVOKE("REVOKE", "回收"),
	SHIELD("SHIELD", "屏蔽");
	private String value;
	private String desc;

	SecGrpAuthorityRevokeType(String value, String desc) {
		this.value = value;
		this.desc = desc;
	}

	public String value() {
		return this.value;
	}

	public String desc() {
		return this.desc;
	}
}
