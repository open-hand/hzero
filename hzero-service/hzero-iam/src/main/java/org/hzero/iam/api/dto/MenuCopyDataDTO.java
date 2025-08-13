package org.hzero.iam.api.dto;

import java.util.List;

import org.hzero.iam.domain.entity.Menu;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 菜单复制数据DTO
 *
 * @author xingxingwu.hand-china.com 2019/10/10 15:21
 */
@ApiModel("菜单复制参数对象")
public class MenuCopyDataDTO {
    @ApiModelProperty(value = "复制的菜单ID", required = true)
    @Encrypt
    private List<Long> copyMenuIds;
	@ApiModelProperty(value = "复制的根目录", required = true)
	private Menu rootMenu;
	@ApiModelProperty("复制的来源租户ID")
	private Long sourceTenantId;
	@ApiModelProperty("复制的目标租户ID")
	private Long targetTenantId;

	public Menu getRootMenu() {
		return rootMenu;
	}

	public void setRootMenu(Menu rootMenu) {
		this.rootMenu = rootMenu;
	}

	public List<Long> getCopyMenuIds() {
		return copyMenuIds;
	}

	public void setCopyMenuIds(List<Long> copyMenuIds) {
		this.copyMenuIds = copyMenuIds;
	}

	public Long getSourceTenantId() {
		return sourceTenantId;
	}

	public void setSourceTenantId(Long sourceTenantId) {
		this.sourceTenantId = sourceTenantId;
	}

	public Long getTargetTenantId() {
		return targetTenantId;
	}

	public void setTargetTenantId(Long targetTenantId) {
		this.targetTenantId = targetTenantId;
	}
}
