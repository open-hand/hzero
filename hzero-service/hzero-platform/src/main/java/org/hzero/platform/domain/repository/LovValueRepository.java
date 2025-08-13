package org.hzero.platform.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p><b>name</b> LovRepository</p>
 * <p><b>description</b> 值集头Repository</p>
 *
 * @author gaokuo.dai@hand-china.com    2018年6月6日上午9:22:18
 * @version 1.0
 */
public interface LovValueRepository extends BaseRepository<LovValue> {

    /**
     * 根据条件查询待插入缓存的数据
     *
     * @param queryParam 查询条件
     * @return 查询结果
     */
    List<LovValue> listLovValueForCache(LovValue queryParam, String lang);

    /**
     * 根据LovCode清除值集值缓存
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @return 是否清除成功
     */
    boolean cleanCache(String lovCode, Long tenantId);

    /**
     * 按主键更新值集
     *
     * @param lovValue lov
     * @return 更新数量
     */
    int updateLovValue(LovValue lovValue);

    /**
     * 按主键删除
     *
     * @param key 主键
     * @return 删除的数量
     */
    @Override
    int deleteByPrimaryKey(Object key);

    /**
     * 从数据库中加载指定lovCode的值集值到缓存
     *
     * @param lovCode  编码
     * @param tenantId 租户Id
     * @param lang     语言
     * @return 指定lovCode的值集值
     */
    List<LovValue> loadValueCacheFromDb(String lovCode, Long tenantId, String lang);

    /**
     * 从数据库中加载指定lovCode的值集值到缓存
     *
     * @param lov         值集
     * @param lang        语言
     * @param pageRequest 分页
     * @return 指定lovCode的值集值
     */
    Page<LovValue> pageLovValue(LovValue lov, String lang, PageRequest pageRequest);

    /**
     * 根据头信息更新行
     *
     * @param header 头信息
     * @return 更新的行数
     */
    int updateLovValueByHeaderInfo(Lov header);

    /**
     * 根据头ID删除行<br/>
     * <i>直接用标准删除的话在头没有行的情况下会报乐观锁异常</i>
     *
     * @param lovId lovId
     * @return 被删除的数量
     */
    int deleteByLovId(Long lovId);

    /**
     * 根据值集ID分页查询值集值
     *
     * @param pageRequest 分页条件
     * @param lovId       值集ID
     * @param tenantId    租户ID
     * @param value       值
     * @param meaning     含义
     * @return 固定值集值
     */
    Page<LovValue> pageAndSortByLovId(PageRequest pageRequest, Long lovId, Long tenantId, String value, String meaning);

    /**
     * 查询与给定代码重复的数据库记录数
     *
     * @param lovValue 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(LovValue lovValue);

    /**
     * 分页查询维度值，启用才可见
     *
     * @param pageRequest 分页参数
     * @param lovId       值集Id
     * @param tenantId    租户Id
     * @return 分页参数
     */
    Page<LovValue> pageAndSortByLovIdForDataGroup(PageRequest pageRequest, Long lovId, Long tenantId);

    /**
     * 批量删除值集值
     *
     * @param lovValues 值集值集合
     */
    void batchDeleteLovValuesByPrimaryKey(List<LovValue> lovValues);
}
