package org.hzero.message.app.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.hzero.message.app.service.EmailFilterService;
import org.hzero.message.domain.entity.EmailFilter;
import org.hzero.message.domain.repository.EmailFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 邮箱账户黑白名单应用服务默认实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
@Service
public class EmailFilterServiceImpl implements EmailFilterService {

    @Autowired
    private EmailFilterRepository emailFilterRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<EmailFilter> createOrUpdateFilter(List<EmailFilter> emailFilterList, Long organizationId) {
        emailFilterList.forEach(item -> {
            if (item.getEmailFilterId() == null) {
                // 新建
                if (StringUtils.isNotBlank(item.getAddress())) {
                    item.setTenantId(organizationId);
                    emailFilterRepository.insertSelective(item);
                }
            } else {
                // 更新
                emailFilterRepository.updateOptional(item, EmailFilter.FIELD_ADDRESS);
            }
        });
        return emailFilterList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFilter(List<EmailFilter> emailFilterList) {
        emailFilterRepository.batchDelete(emailFilterList);
    }
}
