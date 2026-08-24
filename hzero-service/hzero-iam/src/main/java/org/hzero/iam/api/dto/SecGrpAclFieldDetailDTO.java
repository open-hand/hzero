package org.hzero.iam.api.dto;

import java.util.List;
import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.entity.SecGrpAclField;

/**
 * 安全组访问字段权限详情DTO
 *
 * @author xingxingwu.hand-china.com 2019/11/22 14:07
 */
public class SecGrpAclFieldDetailDTO {
	private Permission api;
	private List<SecGrpAclField> fields;

	public SecGrpAclFieldDetailDTO(Permission api, List<SecGrpAclField> fields) {
		this.api = api;
		this.fields = fields;
	}

	public Permission getApi() {
		return api;
	}

	public void setApi(Permission api) {
		this.api = api;
	}

	public List<SecGrpAclField> getFields() {
		return fields;
	}

	public void setFields(List<SecGrpAclField> fields) {
		this.fields = fields;
	}
}
