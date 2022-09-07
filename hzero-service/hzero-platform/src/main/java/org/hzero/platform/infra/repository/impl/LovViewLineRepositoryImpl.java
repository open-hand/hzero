package org.hzero.platform.infra.repository.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.platform.domain.entity.LovViewHeader;
import org.hzero.platform.domain.entity.LovViewLine;
import org.hzero.platform.domain.repository.LovViewHeaderRepository;
import org.hzero.platform.domain.repository.LovViewLineRepository;
import org.hzero.platform.infra.mapper.LovViewLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 值集视图行表仓库实现类
 *
 * @author gaokuo.dai@hand-china.com 2018年6月13日下午5:27:48
 */
@Component
public class LovViewLineRepositoryImpl extends BaseRepositoryImpl<LovViewLine> implements LovViewLineRepository {
    
    @Autowired
    private LovViewLineMapper lovViewLineMapper;
    @Autowired
    private LovViewHeaderRepository lovViewHeaderRepository;

    @Override
    public Page<LovViewLine> pageAndSortLovViewLineByLovId(Long viewHeaderId, Long tenantId, PageRequest pageRequest) {
        LovViewLine queryParam = new LovViewLine();
        queryParam.setViewHeaderId(viewHeaderId);
        return PageHelper.doPageAndSort(pageRequest, () -> this.lovViewLineMapper.selectLovViewLinesByHeaderId(viewHeaderId, tenantId)); 
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByPrimaryKey(LovViewLine record) {
        if(record == null) {
            return 0;
        }
        LovViewHeader viewHeader = this.lovViewHeaderRepository.selectByPrimaryKey(record.getViewHeaderId());
        Assert.notNull(viewHeader, BaseConstants.ErrorCode.DATA_INVALID);
        record.setViewHeaderId(viewHeader.getViewHeaderId());
        int updateCount = super.updateByPrimaryKey(record);
        this.lovViewHeaderRepository.cleanCache(record.getViewHeaderId());
        return updateCount;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKey(Object key) {
        if(key == null) {
            return 0;
        }
        LovViewLine line = this.selectByPrimaryKey(key);
        int updateCount = super.deleteByPrimaryKey(key);
        this.lovViewHeaderRepository.cleanCache(line.getViewHeaderId());
        return updateCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteLovViewLinesByPrimaryKey(List<LovViewLine> lovViewLines) {
        // 批量删除值集视图行
        this.batchDeleteByPrimaryKey(lovViewLines);
        // 清除缓存
        lovViewLines.forEach(lovViewLine -> lovViewHeaderRepository.cleanCache(lovViewLine.getViewHeaderId()));
    }

    @Override
    public List<LovViewLine> selectByHeaderId(Long lovViewHeaderId, String lang) {
        return lovViewLineMapper.selectByHeaderId(lovViewHeaderId, lang);
    }
}
