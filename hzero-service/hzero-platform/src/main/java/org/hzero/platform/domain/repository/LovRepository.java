package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.LovAggregateDTO;
import org.hzero.platform.domain.entity.Lov;

import java.util.List;

/**
 * <p><b>name</b> LovRepository</p>
 * <p><b>description</b> 值集头Repository</p>
 *
 * @author gaokuo.dai@hand-china.com    2018年6月6日上午9:22:18
 * @version 1.0
 */
public interface LovRepository extends BaseRepository<Lov> {

    /**
     * 根据ID或Code查询Lov
     *
     * @param queryParam 查询参数
     * @return lov
     */
    Lov selectByPrimaryKeyOrCode(Lov queryParam);

    /**
     * 根据从缓存中装载租户级Lov
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @param lang     语言
     * @return 缓存中装载的Lov, 没有数据则返回null, 无权访问则会返回一个accessDeniedFlag = 1的空lov
     */
    List<Lov> queryLovFromCacheByTenant(String lovCode, Long tenantId, String lang);


    /**
     * 条件查询Lov头
     *
     * @param queryParam 查询条件
     * @return Lov头
     */
    List<Lov> selectLovHeaders(Lov queryParam);

    /**
     * 根据LovCode清除缓存
     *
     * @param lovCode  值集代码
     * @param tenantId 租户Id
     * @param lang     语言
     * @return 是否删除成功
     */
    boolean cleanCache(String lovCode, Long tenantId, String lang);

    /**
     * 根据LovId清除缓存<br/>
     * <i>此操作会查询数据库,不推荐使用</i>
     *
     * @param lovId 值集ID
     * @return 是否删除成功
     */
    boolean cleanCache(Long lovId);

    /**
     * 按主键更新值集
     *
     * @param lov lov
     * @return 更新的数量
     */
    int updateLov(Lov lov);

    /**
     * 查询与给定代码重复的数据库记录数
     *
     * @param queryParam 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(Lov queryParam);

    /**
     * 按主键删除
     *
     * @param key 主键
     * @return 删除的数量
     */
    @Override
    int deleteByPrimaryKey(Object key);

    /**
     * 分页查询
     *
     * @param record      查询条件
     * @param pageRequest 分页排序条件
     * @return lov
     */
    Page<Lov> pageAndSort(Lov record, PageRequest pageRequest);

    /**
     * 根据值集ID查询值集
     *
     * @param lovId          主键
     * @param tenantId       租户Id
     * @param sqlTypeControl SQL 类型值集，不允许新建，不允许查看SQL
     * @return lov
     */
    Lov selectLovHeaderByLovId(Long lovId, Long tenantId, boolean sqlTypeControl);

    /**
     * 分页查询数据组维度
     *
     * @param record      查询条件
     * @param pageRequest 分页条件
     * @return lov
     */
    Page<Lov> pageLovForDataGroupDimension(Lov record, PageRequest pageRequest);

    /**
     * 获取Redis中缓存的值集或值集视图值，返回值为FORBIDDEN时设置禁止访问
     *
     * @param cachePrefix         缓存前缀
     * @param failFastCachePrefix 缓存前缀
     * @param code                值集或值集视图编码
     * @param tenantId            租户Id
     * @param lang                语言
     * @return 值集或值集视图内容（json格式）
     */
    String getLovOrViewFromRedis(String failFastCachePrefix, String cachePrefix, String code, Long tenantId, String lang);

    List<String> getLovOrViewFromRedis(String cachePrefix, String lovCode, Long tenantId, String lang);

    default String hashKey(Long tenantId) {
        return hashKey(tenantId, null);
    }

    default String hashKey(Long tenantId, String lang) {
        if (StringUtils.isBlank(lang)) {
            return String.valueOf(tenantId);
        }
        return tenantId + "-" + lang;
    }

    /**
     * 通过视图编码和租户Id获取关联的值集信息
     *
     * @param viewCode 视图编码
     * @param tenantId 租户Id
     * @return 关联值集信息
     */
    Lov selectLovByViewCodeAndTenant(String viewCode, Long tenantId);

    /**
     * 聚合获取值集头行数据
     *
     * @param lovCode  值集编码
     * @param tenantId 租户Id
     * @param lang     语言
     * @param tag      标记，仅独立值集可作为查询条件
     * @return 值集头行聚合结果
     */
    LovAggregateDTO selectLovAggregateLovValues(String lovCode, Long tenantId, String lang, String tag);

    /**
     * 根据id和语言查询值集
     *
     * @param lovId 值集ID
     * @param lang  语言
     * @return 值集
     */
    Lov selectByIdAndLang(Long lovId, String lang);

    /**
     * 根据编码、租户和语言查询值集
     *
     * @param lovCode 值集code
     * @param lang    语言
     * @return 值集
     */
    Lov selectByCodeAndLang(String lovCode, Long tenantId, String lang);
}
