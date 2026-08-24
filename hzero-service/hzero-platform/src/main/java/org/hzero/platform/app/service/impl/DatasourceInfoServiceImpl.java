package org.hzero.platform.app.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.boot.platform.form.FormClient;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.hzero.mybatis.util.Sqls;
import org.hzero.platform.app.service.DatasourceInfoService;
import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.entity.DatasourceDriver;
import org.hzero.platform.domain.repository.DatasourceDriverRepository;
import org.hzero.platform.domain.repository.DatasourceRepository;
import org.hzero.platform.domain.service.datasource.DsExtendEntrance;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.enums.DatabaseTypeEnum;
import org.hzero.platform.infra.mapper.DatasourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 数据源配置应用服务默认实现
 *
 * @author like.zhang@hand-china.com 2018-09-13 14:10:13
 */
@Service
public class DatasourceInfoServiceImpl implements DatasourceInfoService {

    private DatasourceRepository dsRepository;
    private DatasourceMapper dsMapper;
    private RedisHelper redisHelper;
    private EncryptClient encryptClient;
    private DsExtendEntrance dsEventPublish;
    private DatasourceDriverRepository driverRepository;
    private LovAdapter lovAdapter;
    private FormClient formClient;

    @Autowired
    public DatasourceInfoServiceImpl(DatasourceRepository dsRepository, DatasourceMapper dsMapper,
            RedisHelper redisHelper, EncryptClient encryptClient, DsExtendEntrance dsEventPublish,
            DatasourceDriverRepository driverRepository, LovAdapter lovAdapter, FormClient formClient) {
        this.dsRepository = dsRepository;
        this.dsMapper = dsMapper;
        this.redisHelper = redisHelper;
        this.encryptClient = encryptClient;
        this.dsEventPublish = dsEventPublish;
        this.driverRepository = driverRepository;
        this.lovAdapter = lovAdapter;
        this.formClient = formClient;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceInfoServiceImpl.class);

    @Override
    public Datasource getByUnique(Long tenantId, String datasourceCode, String dsPurposeCode) {
        Datasource datasource = dsRepository.getByUnique(tenantId, datasourceCode);
        if (datasource == null) {
            // 将“error”写入缓存，用作防击穿
            datasource = new Datasource();
            datasource.setTenantId(tenantId);
            datasource.setDatasourceCode(datasourceCode);
            datasource.setDsPurposeCode(dsPurposeCode);
            datasource.refreshCache(redisHelper);
            return null;
        } else {
            datasource.refreshCache(redisHelper);
            return datasource;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Datasource createDatasource(Datasource datasource) {
        datasource.validateDatasourceCode(dsRepository);
        // 判断是否是自定义驱动，若不是则需要设置默认datasourceUrl
        generateDriverClass(datasource);
        String tempDecryptPassword = null;
        if (StringUtils.isNotBlank(datasource.getPasswordEncrypted())) {
            // 开启数据加密
            DataSecurityHelper.open();
            // 密码解密
            tempDecryptPassword = encryptClient.decrypt(datasource.getPasswordEncrypted());
            datasource.setPasswordEncrypted(tempDecryptPassword);
        }
        dsRepository.insertSelective(datasource);
        datasource.refreshCache(redisHelper);
        // 自定义驱动需发布创建数据源事件消息,需传递明文数据源密码以及数据源驱动信息
        if (Constants.Datasource.CUSTOMIZE.equals(datasource.getDriverType())) {
            DatasourceDriver datasourceDriver = driverRepository.selectByPrimaryKey(datasource.getDriverId());
            datasource.setPasswordEncrypted(tempDecryptPassword);
            // 发布事件
            dsEventPublish.createEventPublish(datasource, datasourceDriver);
        }
        datasource.setPasswordEncrypted(null);
        return datasource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Datasource updateDatasource(Datasource datasource) {
        Assert.notNull(datasource.getDatasourceId(), HpfmMsgCodeConstants.ERROR_DATASOURCE_ID_NULL);
        // 更新之前的数据源
        Datasource originDatasource = dsRepository.selectByPrimaryKey(datasource.getDatasourceId());
        decryptPassword(originDatasource);
        // 判断是否是自定义驱动，若不是则需要设置默认datasourceUrl
        generateDriverClass(datasource);
        String tempDecryptPassword;
        if (StringUtils.isBlank(datasource.getPasswordEncrypted())) {
            tempDecryptPassword = originDatasource.getPasswordEncrypted();
            // 更新数据，不更新密码
            dsRepository.updateOptional(datasource, Datasource.FIELD_DATASOURCE_URL, Datasource.FIELD_REMARK,
                            Datasource.FIELD_DESCRIPTION, Datasource.FIELD_ENABLED_FLAG, Datasource.FIELD_DB_POOL_TYPE,
                            Datasource.FIELD_USERNAME, Datasource.FIELD_OPTIONS, Datasource.FIELD_EXT_CONFIG,
                            Datasource.FIELD_DRIVER_CLASS, Datasource.FIELD_DRIVER_ID);
        } else {
            // 传入新密码，说明密码被修改，先加密密码再更新
            DataSecurityHelper.open();
            tempDecryptPassword = datasource.getPasswordEncrypted();
            // 密码解密
            datasource.setPasswordEncrypted(encryptClient.decrypt(datasource.getPasswordEncrypted()));
            dsRepository.updateOptional(datasource, Datasource.FIELD_DATASOURCE_URL, Datasource.FIELD_REMARK,
                            Datasource.FIELD_DESCRIPTION, Datasource.FIELD_ENABLED_FLAG, Datasource.FIELD_USERNAME,
                            Datasource.FIELD_DB_POOL_TYPE, Datasource.FIELD_OPTIONS, Datasource.FIELD_EXT_CONFIG,
                            Datasource.FIELD_DRIVER_CLASS, Datasource.FIELD_DRIVER_ID,
                            Datasource.FIELD_PASSWORD_ENCRYPTED);
        }
        datasource.clearCache(redisHelper);
        Datasource entity = dsRepository.selectByPrimaryKey(datasource.getDatasourceId());
        // 刷新缓存
        entity.refreshCache(redisHelper);
        if (Constants.Datasource.CUSTOMIZE.equals(datasource.getDriverType())) {
            DatasourceDriver datasourceDriver = driverRepository.selectByPrimaryKey(datasource.getDriverId());
            datasource.setPasswordEncrypted(tempDecryptPassword);
            // 发布事件
            dsEventPublish.updateEventPublish(originDatasource, datasource, datasourceDriver);
        }
        return datasource;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeDatasourceById(Long datasourceId) {
        Datasource datasource = dsRepository.selectByPrimaryKey(datasourceId);
        Assert.notNull(datasource, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        dsMapper.deleteByPrimaryKey(datasourceId);
        datasource.clearCache(redisHelper);

        if (Constants.Datasource.CUSTOMIZE.equals(datasource.getDriverType())) {
            decryptPassword(datasource);
            // 发布删除事件
            DatasourceDriver datasourceDriver = driverRepository.selectByPrimaryKey(datasource.getDriverId());
            // 发布事件
            dsEventPublish.removeEventPublish(datasource, datasourceDriver);
        }
    }

    @Override
    public Map<String, String> getConnectInit(String dbType) {
        Map<String, String> initMap = Maps.newHashMap();
        switch (DatabaseTypeEnum.valueOf2(dbType)) {
            case MYSQL:
            case TIDB:
                initMap.put(Datasource.FIELD_DRIVER_CLASS, Constants.Datasource.DRIVER_CLASS_MYSQL);
                initMap.put(Datasource.FIELD_DATASOURCE_URL, Constants.Datasource.DATA_SOURCE_URL_MYSQL);
                break;
            case ORACLE:
                initMap.put(Datasource.FIELD_DRIVER_CLASS, Constants.Datasource.DRIVER_CLASS_ORACLE);
                initMap.put(Datasource.FIELD_DATASOURCE_URL, Constants.Datasource.DATA_SOURCE_URL_ORACLE);
                break;
            case SQLSERVER:
                initMap.put(Datasource.FIELD_DRIVER_CLASS, Constants.Datasource.DRIVER_CLASS_SQLSERVER);
                initMap.put(Datasource.FIELD_DATASOURCE_URL, Constants.Datasource.DATA_SOURCE_URL_SQLSERVER);
                break;
            case OTHERDB:
            default:
                break;
        }
        return initMap;
    }

    @Override
    public void testConnection(Datasource datasource) {
        Assert.notNull(datasource, BaseConstants.ErrorCode.NOT_NULL);
        // 设置明文密码，用于测试连接
        // 若密码不为空，则传进来的是明文密码，不需要解密，直接获取即可
        if (datasource.getPasswordEncrypted() == null && datasource.getDatasourceId() != null) {
            Datasource dbDatasource = dsRepository.selectByPrimaryKey(datasource.getDatasourceId());
            // 设置数据库密文密码，再解密
            datasource.setPasswordEncrypted(dbDatasource.getPasswordEncrypted());
            decryptPassword(datasource);
        }
        if (datasource.getDriverId() == null && datasource.getDriverClass() == null) {
            generateDriverClass(datasource);
        }
        // 密码解密
        datasource.setPasswordEncrypted(encryptClient.decrypt(datasource.getPasswordEncrypted()));
        dsEventPublish.testConnectionByDatasource(datasource);
    }

    /**
     * 解密密码，若密码为空则查询数据库中的密码进行解密
     */
    private void decryptPassword(Datasource datasource) {
        String decryptPwd;
        try {
            decryptPwd = DataSecurityHelper.decrypt(datasource.getPasswordEncrypted());
            datasource.setPasswordEncrypted(decryptPwd);
        } catch (Exception ex) {
            LOGGER.error("datasource password decrypt failed!");
        }
    }

    /**
     * 判断是否是自定义驱动，若不是则设置datasourceUrl为默认驱动datasourceUrl
     *
     * @param datasource 数据源参数
     */
    @Override
    public void generateDriverClass(Datasource datasource) {
        if (Constants.Datasource.DEFAULT.equals(datasource.getDriverType())) {
            // 设置driverId 为null
            datasource.setDriverId(null);
            // 默认驱动，使用默认驱动包
            Map<String, String> connectInit = getConnectInit(datasource.getDbType());
            if (MapUtils.isNotEmpty(connectInit)) {
                String driverClass = connectInit.get(Datasource.FIELD_DRIVER_CLASS);
                if (datasource.getDriverClass() == null) {
                    // 设置数据源URL
                    datasource.setDriverClass(driverClass);
                }
            }
        }
    }

    @Override
    public Page<Datasource> pageDatasource(PageRequest pageRequest, Datasource datasource, boolean orgQueryFlag) {
        Page<Datasource> datasources = dsRepository.pageDatasource(pageRequest, datasource, orgQueryFlag);
        datasources.getContent().forEach(this::convertDsPurposeCodeToMeaning);
        return datasources;
    }

    @Override
    public Datasource selectDatasource(Long datasourceId) {
        Datasource datasource = dsRepository.selectDatasource(datasourceId);
        this.convertDsPurposeCodeToMeaning(datasource);
        if (datasource.getExtConfig() != null) {
            // 翻译扩展表单行数据
            datasource.setExtConfig(formClient.translateAndProcessFormLineData(datasource.getDbType(), datasource.getTenantId(), datasource.getExtConfig()));
        }
        return datasource;
    }

    @Override
    public List<Datasource> listDatasourceByCondition(Datasource datasource) {
        return dsRepository.listDatasourceByCondition(datasource);
    }

    @Override
    public void initAllData() {
        try {
            SecurityTokenHelper.close();
            List<Datasource> datasources = dsRepository.selectByCondition(Condition.builder(Datasource.class)
                            .andWhere(Sqls.custom()
                                    .andEqualTo(Datasource.FIELD_ENABLED_FLAG, BaseConstants.Flag.YES))
                            .build());
            SecurityTokenHelper.close();
            if (CollectionUtils.isNotEmpty(datasources)) {
                // 添加数据源缓存
                datasources.forEach(ds -> {
                    String[] split = StringUtils.split(ds.getDsPurposeCode(), BaseConstants.Symbol.COMMA);
                    for (String purposeCode : split) {
                        Datasource.pushRedis(ds, redisHelper, purposeCode);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Datasource cache init not successfully completed", e);
        }
        SecurityTokenHelper.clear();
    }

    /**
     *  组装数据源用途meaning，结果为“数据导入,数据报表,接口平台 ...”用','分割
     */
    private void convertDsPurposeCodeToMeaning(Datasource datasource) {
        if (StringUtils.isBlank(datasource.getDsPurposeCode())) {
            return;
        }
        String[] dsPurposeCodes = StringUtils.split(datasource.getDsPurposeCode(), BaseConstants.Symbol.COMMA);
        String dsPurposeMeaning = "";
        for (String dsPurposeCode : dsPurposeCodes) {
            String meaning =
                    lovAdapter.queryLovMeaning(Datasource.DS_PURPOSE_LOV_CODE, datasource.getTenantId(), dsPurposeCode);
            if (StringUtils.isNotBlank(dsPurposeMeaning)) {
                dsPurposeMeaning = StringUtils.join(dsPurposeMeaning, BaseConstants.Symbol.COMMA, meaning);
            } else {
                dsPurposeMeaning = meaning;
            }
        }
        if (StringUtils.isNotBlank(dsPurposeMeaning)) {
            datasource.setDsPurposeCodeMeaning(dsPurposeMeaning);
        }
    }
}
