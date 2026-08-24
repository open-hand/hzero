package org.hzero.iam.infra.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.hzero.iam.domain.entity.MenuTl;
import org.hzero.iam.domain.repository.MenuTlRepository;
import org.hzero.iam.infra.mapper.MenuTlMapper;

/**
 *
 * @author allen 2018/7/5
 */
@Repository
public class MenuTlRepositoryImpl implements MenuTlRepository {
    @Autowired
    private MenuTlMapper menuTlMapper;

    @Override
    public int insert(MenuTl menuTl) {
        return menuTlMapper.insert(menuTl);
    }

    @Override
    public int deleteByMenuId(Long menuId) {
        return menuTlMapper.deleteByMenuId(menuId);
    }
}
