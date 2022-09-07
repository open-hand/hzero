package org.hzero.platform.domain.repository;

import java.io.IOException;
import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.CodeRuleDTO;
import org.hzero.platform.domain.entity.CodeRule;
import org.hzero.platform.domain.entity.CodeRuleDetail;
import org.hzero.platform.domain.entity.CodeRuleDist;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * <p>
 * 编码规则repository
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/13 18:02
 */
public interface CodeRuleRepository extends BaseRepository<CodeRule> {

    /**
     * 删除编码规则
     *
     * @param codeRuleList 编码规则List
     */
    void delete(List<CodeRule> codeRuleList);

    /**
     * 根据主键id查询编码规则信息和编码规则分配信息
     *
     * @param tenantId    租户ID
     * @param codeRuleId  主键id
     * @param pageRequest 分页
     * @return 编码规则
     */
    CodeRuleDTO query(Long tenantId, Long codeRuleId, PageRequest pageRequest);

    /**
     * 查询编码规则dtoList
     *
     * @param codeRule    编码规则
     * @param pageRequest 分页工具类
     * @return 编码规则dtoList
     */
    List<CodeRuleDTO> selectCodeRuleList(CodeRule codeRule, PageRequest pageRequest);

    /**
     * 将编码规则初始化到redis缓存中
     *
     * @param codeRule 编码规则
     */
    void initCache(CodeRule codeRule);

    /**
     * 往redis中新增
     *
     * @param codeRule           编码规则
     * @param codeRuleDist       编码规则分配
     * @param codeRuleDetailList 编码规则明细list
     */
    void initCache(CodeRule codeRule, CodeRuleDist codeRuleDist, List<CodeRuleDetail> codeRuleDetailList);

    /**
     * 往redis中新增
     *
     * @param key                key
     * @param codeRuleDetailList 编码规则明细list
     */
    void initCache(String key, List<CodeRuleDetail> codeRuleDetailList);

    /**
     * 删除redis缓存中的编码规则
     *
     * @param codeRule 编码规则
     */
    void deleteCache(CodeRule codeRule);

    /**
     * 删除redis缓存
     *
     * @param codeRule     编码规则
     * @param codeRuleDist 编码规则分配
     */
    void deleteCache(CodeRule codeRule, CodeRuleDist codeRuleDist);

    /**
     * 根据key删除redis
     *
     * @param key key
     */
    void deleteCache(String key);

    /**
     * 从redis中获取编码规则明细list集合
     *
     * @param key key
     * @return 编码规则明细list
     * @throws IOException IOException
     */
    List<CodeRuleDetail> getCodeRuleListFromCache(String key);

    /**
     * 清空快速失败标记
     *
     * @param tenantId   租户ID
     * @param ruleCode   编码规则编码
     * @param level      编码规则层级
     * @param levelCode  编码规则层级编码
     * @param levelValue 编码规则层级值
     */
    void clearFailFastCache(Long tenantId, String ruleCode, String level, String levelCode, String levelValue);

    /**
     * 清空快速失败标记
     *
     * @param tenantId   租户ID
     * @param ruleCode   编码规则编码
     * @param level      编码规则层级
     * @param levelCode  编码规则层级编码
     * @param levelValue 编码规则层级值
     */
    void clearCache(Long tenantId, String ruleCode, String level, String levelCode, String levelValue);

    /**
     * 通过编码规则明细ID查询编码规则是否存在
     *
     * @param ruleDetailId 编码规则明细ID
     * @return 编码规则
     */
    CodeRule selectCodeRuleByDetailId(Long ruleDetailId);

    /**
     * 获取编码规则信息
     *
     * @param tenantId      租户Id
     * @param ruleCodeList  规则编码集合
     * @return  查询结果
     */
    List<CodeRuleDTO> getCodeRuleList(Long tenantId, List<String> ruleCodeList);
}
