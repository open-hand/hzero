package org.hzero.admin.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.admin.app.service.HystrixConfLineService;
import org.hzero.admin.app.service.HystrixConfService;
import org.hzero.admin.domain.entity.HystrixConf;
import org.hzero.admin.domain.entity.HystrixConfLine;
import org.hzero.admin.domain.entity.ServiceConfig;
import org.hzero.admin.domain.repository.HystrixConfLineRepository;
import org.hzero.admin.domain.repository.HystrixConfRepository;
import org.hzero.admin.domain.repository.ServiceConfigRepository;
import org.hzero.admin.domain.service.ConfigClient;
import org.hzero.admin.infra.constant.Governance;
import org.hzero.core.base.BaseAppService;
import org.hzero.core.base.BaseConstants;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Hystrix保护设置应用服务默认实现
 *
 * @author xingxing.wu@hand-china.com 2018-09-11 14:05:14
 */
@Service
public class HystrixConfServiceImpl extends BaseAppService implements HystrixConfService {

    private static final String DEFAULT_PROFILE = "default";

    @Lazy
    @Autowired
    private ConfigClient configClient;
    @Lazy
    @Autowired
    private HystrixConfLineService hystrixConfLineService;
    @Lazy
    @Autowired
    private HystrixConfRepository hystrixConfRepository;
    @Lazy
    @Autowired
    private HystrixConfLineRepository hystrixConfLineRepository;
    @Lazy
    @Autowired
    private ServiceConfigRepository serviceConfigRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<HystrixConf> batchUpdateSelective(List<HystrixConf> hystrixConfs) {
        this.validList(hystrixConfs);

        if (CollectionUtils.isNotEmpty(hystrixConfs)) {
            //更新头
            for (HystrixConf hystrixConf : hystrixConfs) {
                //判空
                Assert.notNull(hystrixConf, BaseConstants.ErrorCode.DATA_INVALID);
                //判断头是新建还是更新
                if (null == hystrixConf.getConfId()) {
                    //新建头
                    //判断唯一性，代码、类型、服务、服务标签、服务配置Profile的联合唯一约束
                    Assert.isTrue(hystrixConfRepository.countExclusiveSelf(hystrixConf) == 0,
                            BaseConstants.ErrorCode.DATA_EXISTS);
                    hystrixConfRepository.insert(hystrixConf);
                    //若头为新建，行必为新建
                    List<HystrixConfLine> hystrixConfLine = hystrixConf.getHystrixConfLines();
                    if (CollectionUtils.isNotEmpty(hystrixConfLine)) {
                        hystrixConfLineService.batchUpdate(hystrixConfLine, hystrixConf.getConfId());
                    }
                } else {
                    //更新头
                    //检查token
                    SecurityTokenHelper.validToken(hystrixConf, false);
                    //数据库中不存在则返回失败
                    Assert.isTrue(hystrixConfRepository.existsWithPrimaryKey(hystrixConf), BaseConstants.ErrorCode.ERROR);
                    //判断唯一性，代码、类型、服务、服务标签、服务配置Profile的联合唯一约束
                    Assert.isTrue(hystrixConfRepository.countExclusiveSelf(hystrixConf) == 0,
                            BaseConstants.ErrorCode.DATA_EXISTS);
                    //指定那几个字段更新
                    hystrixConfRepository.updateOptional(hystrixConf, HystrixConf.FIELD_REMARK, HystrixConf.FIELD_SERVICE_NAME, HystrixConf.FIELD_SERVICE_CONF_PROFILE, HystrixConf.FIELD_SERVICE_CONF_LABEL, HystrixConf.FIELD_ENABLED_FLAG);
                    //判断行是新建还是更新
                    List<HystrixConfLine> hystrixConfLine = hystrixConf.getHystrixConfLines();
                    if (CollectionUtils.isNotEmpty(hystrixConfLine)) {
                        hystrixConfLineService.batchUpdate(hystrixConfLine, hystrixConf.getConfId());
                    }
                }
            }
        }
        return hystrixConfs;
    }

    @Override
    public List<HystrixConf> refresh(List<HystrixConf> hystrixConfs) {
        List<HystrixConf> resultList = new ArrayList<HystrixConf>();
        ServiceConfig config;
        for (HystrixConf hystrixConf : hystrixConfs) {
            //判断是否存在
            hystrixConf = hystrixConfRepository.selectByPrimaryKey(hystrixConf);
            //判空
            if (hystrixConf == null) {
                continue;
            }
            //远程调用获取配置
            String serviceName = hystrixConf.getServiceName();
            if (StringUtils.isEmpty(hystrixConf.getServiceConfLabel()) ||
                    DEFAULT_PROFILE.equals(hystrixConf.getServiceConfLabel())) {
                config = serviceConfigRepository.selectConfigWithVersion(serviceName, null);
            } else {
                config = serviceConfigRepository.selectConfigWithVersion(serviceName, hystrixConf.getServiceConfLabel());
            }
            //判断获取配置信息是否失败
            if (config == null || config.getConfigValue() == null || config.getServiceConfigId() == null) {
                //写入刷新信息
                hystrixConf.setRefreshStatus(Governance.FAIL_REFRESH);
                hystrixConf.setRefreshMessage(Governance.ERROR_GETCONFIG_MSG);
                hystrixConf.setRefreshTime(new Date());
                resultList.add(hystrixConf);
                //继续下一个循环
                continue;
            }
            Map<String, Object> remoteConfigMap = config.jsonToMap();
            //首先删除头对应的类型的配置
            String headType = hystrixConf.getConfTypeCode();
            Iterator<String> remoteKeyIt = remoteConfigMap.keySet().iterator();
            while (remoteKeyIt.hasNext()) {
                //循环配置Key
                String remoteKey = remoteKeyIt.next();
                if (remoteKey.startsWith(headType)) {
                    //如果是以配置头类型开头，则删除
                    remoteKeyIt.remove();
                }
            }
            //如果头是启用的，则查询行的信息
            if (BaseConstants.Flag.YES.equals(hystrixConf.getEnabledFlag())) {
                //获取配置明细
                HystrixConfLine hystrixConfLine = new HystrixConfLine();
                hystrixConfLine.setConfId(hystrixConf.getConfId());
                List<HystrixConfLine> hystrixConfLines = hystrixConfLineRepository.select(hystrixConfLine);
                //如果行不为空，则循环行配置，并写入到配置文件
                if (CollectionUtils.isNotEmpty(hystrixConfLines)) {
                    for (HystrixConfLine tempHystrixConfLine : hystrixConfLines) {
                        //如果行不为空且行不是禁用，写入到配置文件
                        if (tempHystrixConfLine != null && Governance.ENABLE_FLAG.equals(tempHystrixConfLine.getEnabledFlag())
                                && !StringUtil.isEmpty(tempHystrixConfLine.getPropertyValue())) {
                            //组装KEY
                            String key = headType + "." + hystrixConf.getConfKey() + "." + tempHystrixConfLine.getPropertyName();
                            //写入值
                            remoteConfigMap.put(key, tempHystrixConfLine.getPropertyValue());
                        }
                    }
                    //设置组装后的配置
                    config.mapToJson(remoteConfigMap);
                    config.mapToYaml(remoteConfigMap);
                    serviceConfigRepository.updateOptional(config, ServiceConfig.FIELD_CONFIG_VALUE, ServiceConfig.FIELD_CONFIG_YAML);
                }
            }
            //调用服务更新配置
            publishConfig(config.getServiceConfigId());
            //设置状态
            hystrixConf.setRefreshMessage(Governance.SUCCESS_REFRESH_MSG);
            hystrixConf.setRefreshStatus(Governance.SUCCESS_REFRESH);
            hystrixConf.setRefreshTime(new Date());
            resultList.add(hystrixConf);

        }
        //返回之前刷新到数据库,指定更新
        hystrixConfRepository.batchUpdateOptional(resultList, HystrixConf.REFRESH_TIME, HystrixConf.REFRESH_STATUS, HystrixConf.REFRESH_MESSAGE);
        return resultList;
    }

    private void publishConfig(Long serviceConfigId) {
        ServiceConfig self = serviceConfigRepository.selectSelf(serviceConfigId);

        //发布配置到配置中心
        configClient.publishConfig(self.getServiceCode(), self.getConfigVersion());

    }

    @Override
    public Page<HystrixConf> pageByCondition(PageRequest pageRequest, HystrixConf hystrixConf) {
        return hystrixConfRepository.pageByCondition(pageRequest, hystrixConf);
    }

    @Override
    public HystrixConf selectByConfigId(Long confId) {
        HystrixConf hystrixConf = new HystrixConf();
        hystrixConf.setConfId(confId);
        return hystrixConfRepository.selectOptional(hystrixConf, new Criteria().unSelect(
                HystrixConf.FIELD_CREATED_BY, HystrixConf.FIELD_CREATION_DATE,
                HystrixConf.FIELD_LAST_UPDATED_BY, HystrixConf.FIELD_LAST_UPDATE_DATE
        )).get(0);
    }
}
