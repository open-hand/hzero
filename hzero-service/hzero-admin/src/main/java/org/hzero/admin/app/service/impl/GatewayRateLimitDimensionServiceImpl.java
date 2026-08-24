package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.app.service.GatewayRateLimitDimensionService;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.admin.domain.repository.GatewayRateLimitDimensionRepository;
import org.hzero.admin.domain.repository.GatewayRateLimitLineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author XCXCXCXCX
 * @version 1.0
 * @date 2019/11/20 9:31 上午
 */
@Service
public class GatewayRateLimitDimensionServiceImpl implements GatewayRateLimitDimensionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayRateLimitDimensionServiceImpl.class);

    @Lazy
    @Autowired
    private GatewayRateLimitLineRepository gatewayRateLimitLineRepository;

    @Lazy
    @Autowired
    private GatewayRateLimitDimensionRepository gatewayRateLimitDimensionRepository;

    @Override
    public GatewayRateLimitDimension insertSelective(GatewayRateLimitDimension gatewayRateLimitDimension) {
        validate(gatewayRateLimitDimension);
        gatewayRateLimitDimensionRepository.insertSelective(gatewayRateLimitDimension);
        return gatewayRateLimitDimension;
    }

    @Override
    public Page<GatewayRateLimitDimension> pageByCondition(Long rateLimitLineId, PageRequest pageRequest) {
        return gatewayRateLimitDimensionRepository.pageByCondition(rateLimitLineId, pageRequest);
    }

    @Override
    public GatewayRateLimitDimension selectByPrimaryKey(Long rateLimitDimensionId) {
        return gatewayRateLimitDimensionRepository.selectByPrimaryKey(rateLimitDimensionId);
    }

    @Override
    public GatewayRateLimitDimension updateByPrimaryKeySelective(GatewayRateLimitDimension gatewayRateLimitDimension) {
        gatewayRateLimitDimensionRepository.updateByPrimaryKeySelective(gatewayRateLimitDimension);
        return gatewayRateLimitDimension;
    }

    @Override
    public int deleteByCondition(GatewayRateLimitDimension gatewayRateLimitDimension) {
        return gatewayRateLimitDimensionRepository.deleteByCondition(gatewayRateLimitDimension);
    }

    private void validate(GatewayRateLimitDimension gatewayRateLimitDimension){
        Long rateLimitLineId = gatewayRateLimitDimension.getRateLimitLineId();
        GatewayRateLimitLine line = gatewayRateLimitLineRepository.selectByPrimaryKey(rateLimitLineId);
        if (line == null || !line.getRateLimitDimension().equals(gatewayRateLimitDimension.getRateLimitDimension())){
            LOGGER.error("rate limit line does not exist, or field [RateLimitDimension] is inconsistent.");
            throw new CommonException("hadm.error.validate_dimension_format_error");
        }
    }
}
