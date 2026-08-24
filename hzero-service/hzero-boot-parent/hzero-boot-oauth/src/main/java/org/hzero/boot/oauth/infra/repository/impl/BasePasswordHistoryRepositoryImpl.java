package org.hzero.boot.oauth.infra.repository.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.hzero.boot.oauth.domain.entity.BasePasswordHistory;
import org.hzero.boot.oauth.domain.repository.BasePasswordHistoryRepository;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.common.query.SortType;

/**
 *
 * @author bojiangzhou 2019/08/06
 */
@Component
public class BasePasswordHistoryRepositoryImpl extends BaseRepositoryImpl<BasePasswordHistory> implements BasePasswordHistoryRepository {

    @Override
    public List<String> selectUserHistoryPassword(Long userId) {
        BasePasswordHistory params = new BasePasswordHistory();
        params.setUserId(userId);
        List<BasePasswordHistory> histories = selectOptional(params, new Criteria()
                .where(BasePasswordHistory.FIELD_USER_ID)
                .sort(BasePasswordHistory.FIELD_ID, SortType.DESC)
        );
        return histories.stream().map(BasePasswordHistory::getPassword).collect(Collectors.toList());
    }
}
