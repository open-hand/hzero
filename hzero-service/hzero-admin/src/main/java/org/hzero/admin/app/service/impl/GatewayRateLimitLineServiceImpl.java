package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.api.dto.GatewayRateLimitLineDto;
import org.hzero.admin.app.service.GatewayRateLimitLineService;
import org.hzero.admin.app.service.ServiceRouteRefreshService;
import org.hzero.admin.domain.entity.GatewayRateLimitDimension;
import org.hzero.admin.domain.entity.GatewayRateLimitLine;
import org.hzero.admin.domain.entity.ServiceRoute;
import org.hzero.admin.domain.repository.GatewayRateLimitDimensionRepository;
import org.hzero.admin.domain.repository.GatewayRateLimitLineRepository;
import org.hzero.admin.domain.repository.ServiceRouteRepository;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hzero.admin.app.service.GatewayRateLimitService.RATE_LIMITER_FILTER_NAME;

/**
 * 网关限流设置行明细应用服务默认实现
 *
 * @author jianbo.li@china-hand.com 2018-09-10 21:40:45
 */
@Service
public class GatewayRateLimitLineServiceImpl extends BaseAppService implements GatewayRateLimitLineService {

    @Lazy
    @Autowired
    private ServiceRouteRefreshService routeRefreshService;

    @Lazy
    @Autowired
    private GatewayRateLimitLineRepository gatewayRateLimitLineRepository;

    @Lazy
    @Autowired
    private ServiceRouteRepository serviceRouteRepository;

    @Lazy
    @Autowired
    private GatewayRateLimitDimensionRepository gatewayRateLimitDimensionRepository;

    @ProcessLovValue
    @Override
    public Page<GatewayRateLimitLine> pageByCondition(GatewayRateLimitLineDto dto, PageRequest pageRequest) {
        GatewayRateLimitLine line = new GatewayRateLimitLine();
        BeanUtils.copyProperties(dto, line);
        return gatewayRateLimitLineRepository.pageByCondition(pageRequest, line);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<GatewayRateLimitLine> batchInsertOrUpdate(List<GatewayRateLimitLine> gatewayRateLimitLineList) {
        this.validList(gatewayRateLimitLineList);

        List<GatewayRateLimitLine> updateList = new ArrayList<>();
        List<GatewayRateLimitLine> insertList = new ArrayList<>();
        for (GatewayRateLimitLine line : gatewayRateLimitLineList) {
            if (line.getRateLimitLineId() != null) {
                updateList.add(line);
            } else {
                insertList.add(line);
            }
        }
        //校验更新的token
        SecurityTokenHelper.validToken(updateList);
        List<GatewayRateLimitLine> result = new ArrayList<>();
        result.addAll(gatewayRateLimitLineRepository.batchInsert(insertList));
        result.addAll(gatewayRateLimitLineRepository.batchUpdateOptional(updateList));
        return result;
    }

    @ProcessLovValue
    @Override
    public GatewayRateLimitLine detail(Long rateLimitLineId) {
        GatewayRateLimitLine line = gatewayRateLimitLineRepository.selectByPrimaryKey(rateLimitLineId);
        if (line == null) {
            throw new CommonException("hadm.error.record_not_found");
        }
        Long routeId = line.getServiceRouteId();
        ServiceRoute route = serviceRouteRepository.selectByPrimaryKey(routeId);
        if (route == null) {
            line.setPath("无效路由");
        } else {
            line.setPath(route.getPath());
        }
        return line;
    }

    @Override
    public int batchDelete(List<GatewayRateLimitLine> gatewayRateLimitLines) {
        SecurityTokenHelper.validToken(gatewayRateLimitLines);

        int count = gatewayRateLimitLineRepository.batchDelete(gatewayRateLimitLines);
        routeRefreshService.removeRouteExtendConfigAndNotifyGateway(
                RATE_LIMITER_FILTER_NAME,
                gatewayRateLimitLines.stream()
                        .map(GatewayRateLimitLine::getServiceRouteId)
                        .collect(Collectors.toList()));
        return count;
    }

    @Override
    public boolean allowChange(Long rateLimitLineId) {
        GatewayRateLimitDimension queryParam = new GatewayRateLimitDimension();
        queryParam.setRateLimitLineId(rateLimitLineId);
        int count = gatewayRateLimitDimensionRepository.selectCount(queryParam);
        if (count == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean allowChange(GatewayRateLimitLine rateLimitLine) {
        GatewayRateLimitLine record = gatewayRateLimitLineRepository.selectByPrimaryKey(rateLimitLine.getRateLimitLineId());
        if (StringUtils.isEmpty(record.getRateLimitDimension())
                && StringUtils.isEmpty(rateLimitLine.getRateLimitDimension())) {
            return true;
        } else if (record.getRateLimitDimension() != null && record.getRateLimitDimension().equals(rateLimitLine.getRateLimitDimension())) {
            return true;
        }

        GatewayRateLimitDimension queryParam = new GatewayRateLimitDimension();
        queryParam.setRateLimitLineId(rateLimitLine.getRateLimitLineId());
        int count = gatewayRateLimitDimensionRepository.selectCount(queryParam);
        if (count == 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean allowChange(List<GatewayRateLimitLine> rateLimitLines) {
        for (GatewayRateLimitLine line : rateLimitLines) {
            if (!allowChange(line)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public GatewayRateLimitLine updateByPrimaryKey(GatewayRateLimitLine gatewayRateLimitLine) {
        this.validObject(gatewayRateLimitLine);
        SecurityTokenHelper.validToken(gatewayRateLimitLine);
        if (!allowChange(gatewayRateLimitLine)) {
            throw new CommonException("hadm.error.dimension_exists");
        }
        if (BaseConstants.Flag.NO.equals(gatewayRateLimitLine.getEnabledFlag())) {
            // 禁用就移除路由配置
            routeRefreshService.removeRouteExtendConfigAndNotifyGateway(RATE_LIMITER_FILTER_NAME,
                    Collections.singletonList(gatewayRateLimitLine.getServiceRouteId()));
        }
        gatewayRateLimitLineRepository.updateByPrimaryKeySelective(gatewayRateLimitLine);
        return gatewayRateLimitLine;
    }

    @Override
    public List<GatewayRateLimitLine> batchUpdateByPrimaryKey(List<GatewayRateLimitLine> gatewayRateLimitLines) {
        if (!allowChange(gatewayRateLimitLines)) {
            throw new CommonException("hadm.error.dimension_exists");
        }
        return gatewayRateLimitLineRepository.batchUpdateByPrimaryKey(gatewayRateLimitLines);
    }

    @Override
    public GatewayRateLimitLine create(GatewayRateLimitLine gatewayRateLimitLine) {
        Long routeId = gatewayRateLimitLine.getServiceRouteId();
        int count = gatewayRateLimitLineRepository.selectCount(new GatewayRateLimitLine().setServiceRouteId(routeId));
        if (count > 0) {
            throw new CommonException("hadm.error.route_exists_rate_limit");
        }
        gatewayRateLimitLineRepository.insertSelective(gatewayRateLimitLine);
        return gatewayRateLimitLine;
    }

    @Override
    public GatewayRateLimitLine insertSelective(GatewayRateLimitLine gatewayRateLimitLine) {
        this.validObject(gatewayRateLimitLine);
        gatewayRateLimitLineRepository.insertSelective(gatewayRateLimitLine);
        return gatewayRateLimitLine;
    }
}
