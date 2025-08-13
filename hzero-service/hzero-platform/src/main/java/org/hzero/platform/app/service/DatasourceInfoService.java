package org.hzero.platform.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.platform.domain.entity.Datasource;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 数据源配置应用服务
 *
 * @author like.zhang@hand-china.com 2018-09-13 14:10:13
 */
public interface DatasourceInfoService {

    /**
     * 查询数据源
     *
     * @param tenantId       租户
     * @param datasourceCode 数据源编码
     * @param dsPurposeCode  数据源用途
     * @return 数据源
     */
    Datasource getByUnique(Long tenantId, String datasourceCode, String dsPurposeCode);

    /**
     * 创建数据源配置
     *
     * @param datasource 数据源配置
     * @return 数据源配置
     */
    Datasource createDatasource(Datasource datasource);

    /**
     * 更新数据源配置
     *
     * @param datasource 数据源配置
     * @return 数据源配置
     */
    Datasource updateDatasource(Datasource datasource);

    /**
     * 根据数据源id删除数据源配置
     *
     * @param datasourceId 数据源配置id
     */
    void removeDatasourceById(Long datasourceId);

    /**
     * 初始化缓存
     */
    void initAllData();

    /**
     * 根据数据库类型获取初始化信息
     *
     * @param dbType 数据库类型
     * @return Map
     */
    Map<String, String> getConnectInit(String dbType);

    /**
     * 校验数据源连接
     *
     * @param datasource 数据源参数
     */
    void testConnection(Datasource datasource);

    /**
     * 非自定义驱动时生成数据源Url
     *
     * @param datasource 数据源参数
     */
    void generateDriverClass(Datasource datasource);

    /**
     * 分页查询数据源信息
     *
     * @param pageRequest 分页参数
     * @param datasource 查询条件
     * @param orgQueryFlag 租户查询标识
     * @return
     */
    Page<Datasource> pageDatasource(PageRequest pageRequest, Datasource datasource, boolean orgQueryFlag);

    /**
     * 获取数据源信息
     *
     * @param datasourceId 数据源Id
     * @return 数据源明细
     */
    Datasource selectDatasource(Long datasourceId);

    /**
     * 条件查询所有数据源信息
     *
     * @param datasource 数据源查询条件
     * @return 查询结果
     */
    List<Datasource> listDatasourceByCondition(Datasource datasource);
}
