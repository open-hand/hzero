package org.hzero.platform.app.service.impl;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.boot.platform.data.hierarchy.DataHierarchyValueHandler;
import org.hzero.core.algorithm.tree.TreeBuilder;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.TokenUtils;
import org.hzero.platform.api.dto.DataHierarchyDTO;
import org.hzero.platform.api.dto.DataHierarchyDisplayStyleDTO;
import org.hzero.platform.app.service.DataHierarchyService;
import org.hzero.platform.app.service.DataHierarchySwitchService;
import org.hzero.platform.domain.entity.DataHierarchy;
import org.hzero.platform.domain.repository.DataHierarchyRepository;
import org.hzero.platform.infra.constant.DataHierarchyDisplayStyle;
import org.hzero.platform.infra.feign.UserDetailRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据层级配置 切换
 *
 * @author qingsheng.chen@hand-china.com
 */
@Service
public class DataHierarchySwitchServiceImpl implements DataHierarchySwitchService {
    private DataHierarchyService dataHierarchyService;
    private DataHierarchyRepository dataHierarchyRepository;
    private UserDetailRemoteService userDetailRemoteService;
    private List<DataHierarchyValueHandler> dataHierarchyValueHandlerList;

    @Autowired
    public DataHierarchySwitchServiceImpl(DataHierarchyService dataHierarchyService,
                                          DataHierarchyRepository dataHierarchyRepository,
                                          UserDetailRemoteService userDetailRemoteService,
                                          @Autowired(required = false) List<DataHierarchyValueHandler> dataHierarchyValueHandlerList) {
        this.dataHierarchyService = dataHierarchyService;
        this.dataHierarchyRepository = dataHierarchyRepository;
        this.userDetailRemoteService = userDetailRemoteService;
        this.dataHierarchyValueHandlerList = dataHierarchyValueHandlerList;
        if (CollectionUtils.isNotEmpty(this.dataHierarchyValueHandlerList)) {
            this.dataHierarchyValueHandlerList.sort(Comparator.comparingInt(DataHierarchyValueHandler::getOrder));
        }
    }

    @Override
    public List<DataHierarchyDTO> treeDataHierarchyValue(long tenantId) {
        List<DataHierarchyDTO> dataHierarchyList = dataHierarchyService.treeDataHierarchy(tenantId,
                null, null, BaseConstants.Flag.YES);
        readAdditionInfo(Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new), dataHierarchyList);
        return dataHierarchyList;
    }

    @Override
    public DataHierarchyDisplayStyleDTO displayStyleDataHierarchyValue(Long tenantId) {
        List<DataHierarchyDTO> dataHierarchyList = dataHierarchyService.listDataHierarchy(tenantId,
                null, null, BaseConstants.Flag.YES);
        readAdditionInfo(Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new), dataHierarchyList);
        Map<String, List<DataHierarchyDTO>> dataHierarchyMap = dataHierarchyList.stream()
                .collect(Collectors.groupingBy(DataHierarchyDTO::getDisplayStyle));
        return new DataHierarchyDisplayStyleDTO()
                .setSelectList(dataHierarchyMap.get(DataHierarchyDisplayStyle.SELECT.name()))
                .setModalList(dataHierarchyMap.containsKey(DataHierarchyDisplayStyle.MODAL.name())
                        ? TreeBuilder.buildTree(dataHierarchyMap.get(DataHierarchyDisplayStyle.MODAL.name()),
                        null,
                        DataHierarchyDTO::getDataHierarchyId,
                        DataHierarchyDTO::getParentId)
                        : null);
    }

    @Override
    public DataHierarchyDTO queryDataHierarchyValue(long tenantId, String dataHierarchyCode) {
        DataHierarchyDTO dataHierarchy = dataHierarchyService.getDataHierarchy(tenantId, dataHierarchyCode);
        if (dataHierarchy != null) {
            CustomUserDetails customUserDetails = Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new);
            dataHierarchy.setDataHierarchyValue(customUserDetails.readAdditionInfo(dataHierarchy.getDataHierarchyCode()))
                    .setDataHierarchyMeaning(customUserDetails.readAdditionInfoMeaning(dataHierarchy.getDataHierarchyCode()));
        }
        return dataHierarchy;
    }

    private void readAdditionInfo(CustomUserDetails customUserDetails, List<DataHierarchyDTO> dataHierarchyList) {
        if (CollectionUtils.isNotEmpty(dataHierarchyList)) {
            dataHierarchyList.forEach(dataHierarchy -> {
                dataHierarchy.setDataHierarchyValue(customUserDetails.readAdditionInfo(dataHierarchy.getDataHierarchyCode()))
                        .setDataHierarchyMeaning(customUserDetails.readAdditionInfoMeaning(dataHierarchy.getDataHierarchyCode()));
                readAdditionInfo(customUserDetails, dataHierarchy.getChildren());
            });
        }
    }

    @Override
    public Map<String, Object> queryDataHierarchyValue(Set<String> dataHierarchyCodeList) {
        Map<String, Object> dataHierarchyValue = Optional.ofNullable(Optional.ofNullable(DetailsHelper.getUserDetails())
                .orElseThrow(NotLoginException::new)
                .getAdditionInfo())
                .orElse(Collections.emptyMap());
        if (CollectionUtils.isEmpty(dataHierarchyCodeList)) {
            return dataHierarchyValue;
        }
        Map<String, Object> dataHierarchyFilterValue = new HashMap<>();
        dataHierarchyCodeList.forEach(dataHierarchyCode ->
                dataHierarchyFilterValue.put(dataHierarchyCode, dataHierarchyValue.get(dataHierarchyCode))
        );
        return dataHierarchyFilterValue;
    }

    @Override
    public void saveDataHierarchyValue(long tenantId,
                                       String dataHierarchyCode,
                                       String dataHierarchyValue,
                                       String dataHierarchyMeaning) {
        if (CollectionUtils.isNotEmpty(this.dataHierarchyValueHandlerList)) {
            for (DataHierarchyValueHandler dataHierarchyValueHandler : dataHierarchyValueHandlerList) {
                String handleValue = dataHierarchyValueHandler.valueHandler(DetailsHelper.getUserDetails(), tenantId, dataHierarchyCode, dataHierarchyValue, dataHierarchyMeaning);
                dataHierarchyValue = StringUtils.hasText(handleValue) ? handleValue : dataHierarchyValue;
                String handleMeaning = dataHierarchyValueHandler.meaningHandler(DetailsHelper.getUserDetails(), tenantId, dataHierarchyCode, dataHierarchyValue, dataHierarchyMeaning);
                dataHierarchyMeaning = StringUtils.hasText(handleMeaning) ? handleMeaning : dataHierarchyMeaning;
            }
        }
        userDetailRemoteService.storeUserAdditionInfo(TokenUtils.getToken(), dataHierarchyCode, dataHierarchyValue, dataHierarchyMeaning,
                dataHierarchyRepository.listDataHierarchyChildren(tenantId, dataHierarchyCode)
                        .stream()
                        .map(DataHierarchy::getDataHierarchyCode)
                        .collect(Collectors.toList()));
    }
}
