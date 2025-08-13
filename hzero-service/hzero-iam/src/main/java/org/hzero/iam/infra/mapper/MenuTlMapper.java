package org.hzero.iam.infra.mapper;

import org.hzero.iam.domain.entity.MenuTl;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 描述
 * </p>
 *
 * @author allen 2018/7/5
 */
public interface MenuTlMapper extends BaseMapper<MenuTl> {

    /**
     * 新增
     * @param menuTl
     * @return
     */
    @Override
    int insert(MenuTl menuTl);

    /**
     * 删除
     * @param id
     * @return
     */
    int deleteByMenuId(Long id);
}
