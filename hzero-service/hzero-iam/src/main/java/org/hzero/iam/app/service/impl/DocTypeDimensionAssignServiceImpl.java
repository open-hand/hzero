package org.hzero.iam.app.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.DocTypeDimensionAssignService;
import org.hzero.iam.domain.entity.DocTypeDimension;
import org.hzero.iam.domain.repository.DocTypeDimensionRepository;
import org.hzero.iam.domain.repository.SecGrpDclDimRepository;
import org.hzero.iam.infra.constant.Constants;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qingsheng.chen@hand-china.com 2019-07-02 14:36
 */
@Service
public class DocTypeDimensionAssignServiceImpl implements DocTypeDimensionAssignService {
    private final DocTypeDimensionRepository docTypeDimensionRepository;

    @Autowired
    private SecGrpDclDimRepository secGrpDclDimRepository;

    @Autowired
    public DocTypeDimensionAssignServiceImpl(DocTypeDimensionRepository docTypeDimensionRepository) {
        this.docTypeDimensionRepository = docTypeDimensionRepository;
    }

    @Override
    public List<DocTypeDimension> listRoleAssign(long roleId, long tenantId) {
        return docTypeDimensionRepository.listBizDocTypeDimension(tenantId);
    }

    @Override
    public List<DocTypeDimension> listUserAssign(long userId, long tenantId) {
        return docTypeDimensionRepository.listBizDocTypeDimension(tenantId);
    }

    @Override
    public List<DocTypeDimension> listSecGrpAssign(long secGrpId) {
        Set<String> authTypeCodes = secGrpDclDimRepository.listSecGrpAssignedAuthTypeCode(secGrpId);
        return listDimension(authTypeCodes);
    }

    private List<DocTypeDimension> listDimension(Set<String> authTypeCodes) {
        if (CollectionUtils.isNotEmpty(authTypeCodes)) {
            List<DocTypeDimension> docTypeDimensions = docTypeDimensionRepository
                    .listDocTypeDimensionBydimensionCodes(new ArrayList<>(authTypeCodes));


            return docTypeDimensions.stream()
                    .sorted(Comparator.comparing(DocTypeDimension::getOrderSeq)
                            .thenComparing(DocTypeDimension::getDimensionId))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
