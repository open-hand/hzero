package org.hzero.mybatis.base;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseService;
import org.hzero.core.exception.OptimisticLockException;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.domian.Condition;

import java.util.List;

/**
 * 基础Repository
 *
 * @param <T> DO类型
 * @author gaokuo.dai@hand-china.com 2018年6月7日下午5:55:24
 */
public interface BaseRepository<T> extends BaseService<T> {

    /**
     * 默认版本号
     */
    Long DEFAULT_OBJECT_VERSION_NUMBER = 1L;

    /**
     * 默认用户ID
     */
    Long DEFAULT_USER_ID = 1L;

    /**
     * 按照主键更新记录，更新所有字段
     *
     * @param record 更新的记录内容
     * @return 更新记录数量，必定大于1
     * @throws OptimisticLockException <strong>当更新记录数量为0并且记录继承自AuditDomain时</strong>，抛出异常记录不存在或者版本不一致
     */
    @Override
    int updateByPrimaryKey(T record);

    /**
     * 按照主键更新记录，更新非null字段
     *
     * @param record 更新的记录内容
     * @return 更新记录数量，必定大于1
     * @throws OptimisticLockException <strong>当更新记录数量为0并且记录继承自AuditDomain时</strong>，抛出异常记录不存在或者版本不一致
     */
    @Override
    int updateByPrimaryKeySelective(T record);

    /**
     * 分页排序查询
     *
     * @param pageRequest 分页排序条件
     * @param queryParam  查询条件
     * @return
     */
    default Page<T> pageAndSort(PageRequest pageRequest, T queryParam) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.select(queryParam));
    }

    /**
     * 分页排序查询
     *
     * @param pageRequest 分页排序条件
     * @param key         field名
     * @param value       值
     * @return 查询结果
     */
    default Page<T> pageAndSort(PageRequest pageRequest, String key, Object value) {
        return PageHelper.doPageAndSort(pageRequest, () -> this.select(key, value));
    }

    /**
     * 根据key和value查询
     *
     * @param key   字段名称
     * @param value 值
     * @return 查询结果
     */
    List<T> select(String key, Object value);

    /**
     * 批量插入
     *
     * @param list 准备插入的list
     * @return 插入之后的list
     */
    List<T> batchInsert(List<T> list);

    /**
     * 批量插入
     *
     * @param list 准备插入的list
     * @return 插入之后的list
     */
    List<T> batchInsertSelective(List<T> list);

    /**
     * 按主键批量更新
     *
     * @param list 准备更新的list
     * @return 更新之后的list
     */
    List<T> batchUpdateByPrimaryKey(List<T> list);

    /**
     * 按主键选择性批量更新
     *
     * @param list 准备更新的list
     * @return 更新之后的list
     */
    List<T> batchUpdateByPrimaryKeySelective(List<T> list);

    /**
     * 批量选择更新
     *
     * @param list      准备更新的list
     * @param optionals 更新字段
     * @return 更新之后的list
     */
    List<T> batchUpdateOptional(List<T> list, String... optionals);

    /**
     * 批量删除
     *
     * @param list 准备删除的list
     * @return 删除数量
     */
    int batchDelete(List<T> list);

    /**
     * 按主键批量删除
     *
     * @param list 准备删除的list
     * @return 删除数量
     */
    int batchDeleteByPrimaryKey(List<T> list);

    /**
     * 批量保存
     *
     * @param list 准备保存的list
     * @return 保存数量
     */
    int batchSave(List<T> list);

    /**
     * 批量保存
     *
     * @param list 准备保存的list
     * @return 保存数量
     */
    int batchSaveSelective(List<T> list);

    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ids 如 "1,2,3,4"
     * @return 返回列表
     */
    List<T> selectByIds(String ids);

    /**
     * 根据Condition条件进行查询
     *
     * @param condition 查询条件
     * @return 返回列表
     */
    List<T> selectByCondition(Condition condition);

    /**
     * 根据Condition条件进行查询总数
     *
     * @param condition 查询条件
     * @return 数量
     */
    int selectCountByCondition(Condition condition);

    /**
     * 根据指定的字段查询
     *
     * @param condition 查询条件
     * @param criteria  查询条件
     * @return 查询结果
     */
    List<T> selectOptional(T condition, Criteria criteria);

    /**
     * 根据指定的字段查询
     *
     * @param condition 查询条件
     * @param criteria  查询条件
     * @return 更新记录数
     */
    default int deleteOptional(T condition, Criteria criteria) {
        List<T> recodes = selectOptional(condition, criteria);
        return batchDeleteByPrimaryKey(recodes);
    }

    /**
     * 根据指定的字段查询
     *
     * @param condition 查询条件
     * @param criteria  查询条件
     * @return 查询结果
     */
    T selectOneOptional(T condition, Criteria criteria);

}
