package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.MenuTl;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/5
 */
public interface MenuTlRepository {
    /**
     * 新增
     * @param menuTl
     * @return
     */
    int insert(MenuTl menuTl);

    /**
     * 依据ID删除
     * @param menuId
     * @return
     */
    int deleteByMenuId(Long menuId);
}
