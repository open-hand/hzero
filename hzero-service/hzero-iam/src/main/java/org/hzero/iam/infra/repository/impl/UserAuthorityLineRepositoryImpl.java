package org.hzero.iam.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.api.dto.UserAuthorityDataDTO;
import org.hzero.iam.domain.entity.UserAuthorityLine;
import org.hzero.iam.domain.repository.UserAuthorityLineRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.UserAuthorityLineMapper;
import org.hzero.iam.infra.util.BatchSqlHelper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 用户权限管理行表 资源库实现
 *
 * @author liang.jin@hand-china.com 2018-07-31 15:45:42
 */
@Component
public class UserAuthorityLineRepositoryImpl extends BaseRepositoryImpl<UserAuthorityLine> implements UserAuthorityLineRepository {
    
    @Autowired
    private UserAuthorityLineMapper userAuthorityLineMapper;

    @Override
    public Page<UserAuthorityLine> selectCreateUserAuthorityLines(Long authorityId, Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> userAuthorityLineMapper.selectCreateUserAuthorityLines(authorityId, tenantId, dataCode, dataName));
    }

    @Override
    public void updateUserAuthorityLine(Long tenantId, Long authorityId, Long copyAuthorityId) {
        List<UserAuthorityLine> diffUserAuthLines = userAuthorityLineMapper.queryUserAuthLineDiff(tenantId,authorityId,copyAuthorityId);
        if(CollectionUtils.isEmpty(diffUserAuthLines)) {
            return;
        }
        UserUtils.setDataUser(diffUserAuthLines);

        BatchSqlHelper.batchExecute(diffUserAuthLines, 8,
                (dataList) -> userAuthorityLineMapper.updateUserAuthorityLine(dataList),
                "BatchInsertUserAuthorityLine");
    }

    @Override
    public Page<UserAuthorityDataDTO> listPurCat(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listPurCat(tenantId, userId, dataCode, dataName));
    }

    @Override
    public Page<UserAuthorityDataDTO> pageLov(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listLov(tenantId, userId, dataCode, dataName));
    }

    @Override
    public Page<UserAuthorityDataDTO> pageLovView(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listLovView(tenantId, userId, dataCode, dataName));
    }

    @Override
    public Page<UserAuthorityDataDTO> pageDatasource(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> userAuthorityLineMapper.listDatasource(tenantId, userId, dataCode, dataName));
    }

    @Override
    public Page<UserAuthorityDataDTO> pageGroupData(Long tenantId, Long userId, String groupCode, String groupName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listDataGroup(tenantId,userId,groupCode,groupName));
    }

    @Override
    public Page<UserAuthorityDataDTO> listPurCatAll(Long tenantId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listPurCatAll(tenantId,dataCode,dataName));
    }

    @Override
    public void updateUserAuthorityLineDataSource(Long userAuthorityId, String dataSouce) {
        if(userAuthorityId == null) {
            return;
        }

        UserAuthorityLine userAuthorityLine = new UserAuthorityLine();
        userAuthorityLine.setAuthorityId(userAuthorityId);
        List<UserAuthorityLine> userAuthorityLines = userAuthorityLineMapper.select(userAuthorityLine);
        for(UserAuthorityLine authorityLineSnap : userAuthorityLines) {
            authorityLineSnap.setDataSource(dataSouce);
        }
       batchUpdateByPrimaryKeySelective(userAuthorityLines);
    }

    @Override
    public List<Long> selectCompanyAssignOu(Long userId, Long tenantId, Long companyId) {
        return userAuthorityLineMapper.selectCompanyAssignOu(userId, tenantId, companyId);
    }

    @Override
    public List<Long> selectOuAssignInvOrg(Long userId, Long tenantId, List<Long> ouIds) {
        return userAuthorityLineMapper.selectOuAssignInvOrg(userId, tenantId, ouIds);
    }

    @Override
    public Page<UserAuthorityDataDTO> listPurAgent(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listPurAgent(tenantId, userId, dataCode, dataName));
    }

    @Override
    public Page<UserAuthorityDataDTO> listPurOrg(Long tenantId, Long userId, String dataCode, String dataName, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest,()->userAuthorityLineMapper.listPurOrg(tenantId, userId, dataCode, dataName));
    }

}
