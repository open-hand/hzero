package org.hzero.message.domain.entity;

import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

public class Unit {
	
	public static final String FIELD_UNIT_ID = "unitId";
	
	//@ApiModelProperty("部门Id")
	@Id
	@GeneratedValue
	@Encrypt
    private Long unitId;
    //@ApiModelProperty("租户Id")
    private Long tenantId;
    
    private String unitName;
    
    private String unitCode;

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenantId(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

}
