package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.MaintainService;
import org.hzero.admin.domain.entity.Maintain;
import org.hzero.admin.domain.entity.MaintainTable;
import org.hzero.admin.domain.repository.MaintainRepository;
import org.hzero.admin.domain.repository.MaintainTableRepository;
import org.hzero.core.base.BaseAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 1:34 下午
 */
@Service
public class MaintainServiceImpl extends BaseAppService implements MaintainService {

    @Autowired
    private MaintainRepository maintainRepository;
    @Autowired
    private MaintainTableRepository maintainTableRepository;

    /**
     * Thread Unsafe
     * 并发时会导致多个运维版本处于运维中，造成运维混乱
     */
    @Override
    public void updateState(Long maintainId, String from, String to) {
        Maintain maintain = maintainRepository.selectByPrimaryKey(maintainId);
        if (!Objects.equals(maintain.getState(), from)) {
            throw new CommonException("error.state.changed");
        }
        //只允许一个运维版本在运维中
        if (Maintain.State.ACTIVE.name().equals(to)) {
            Maintain param = new Maintain();
            param.setState(Maintain.State.ACTIVE.name());
            List<Maintain> maintains = maintainRepository.select(param);
            if (!CollectionUtils.isEmpty(maintains)) {
                throw new CommonException("error.state.active_exist");
            }
        }
        maintainRepository.updateByPrimaryKey(maintain.setState(to));
    }

    @Override
    public void insertSelective(Maintain record) {
        validObject(record);
        validateUnique(record);
        maintainRepository.insertSelective(record);
    }

    @Override
    public Page<Maintain> page(PageRequest pageRequest, Maintain setState) {
        return maintainRepository.pageAndSort(pageRequest, setState);
    }

    @Override
    public Maintain selectByPrimaryKey(Long maintainId) {
        return maintainRepository.selectByPrimaryKey(maintainId);
    }

    @Override
    public void updateByPrimaryKeySelective(Maintain maintain) {
        maintainRepository.updateByPrimaryKeySelective(maintain);
    }

    @Override
    public void deleteByPrimaryKey(Long maintainId) {
        maintainRepository.deleteByPrimaryKey(maintainId);
    }

    @Override
    public List<String> getServices(Long maintainId) {
        List<MaintainTable> tables = maintainTableRepository.selectMaintainTables(maintainId, null);
        if (CollectionUtils.isEmpty(tables)) {
            return Collections.emptyList();
        }
        return tables.stream()
                .map(MaintainTable::getServiceCode)
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateUnique(Maintain record) {
        Maintain queryParam = new Maintain();
        queryParam.setMaintainVersion(record.getMaintainVersion());
        Maintain old = maintainRepository.selectOne(queryParam);
        if (old != null) {
            throw new CommonException("error.unique.maintain_version");
        }
    }
}
