package org.hzero.iam.infra.constant;

/**
 * <p>
 * 菜单查询范围, STANDARD/CUSTOM/BOTH, 仅查询标准菜单/仅查询租户客户化菜单/二者同时查询
 * </p>
 *
 * @author mingwei.liu@hand-china.com 2018/10/29
 */
public interface HiamMenuScope {
    /**
     * 仅查询标准菜单
     */
    String STANDARD = "STANDARD";
    /**
     * 仅查询租户客户化菜单
     */
    String CUSTOM = "CUSTOM";
    /**
     * 同时查询所有菜单
     */
    String BOTH = "BOTH";
}
