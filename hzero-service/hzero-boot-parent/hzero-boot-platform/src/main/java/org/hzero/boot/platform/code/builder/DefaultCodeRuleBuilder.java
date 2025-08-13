package org.hzero.boot.platform.code.builder;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.hzero.boot.platform.code.autoconfigure.CodeRuleProperties;
import org.hzero.boot.platform.code.constant.CodeConstants;
import org.hzero.boot.platform.code.entity.CodeRuleDetail;
import org.hzero.boot.platform.code.entity.SimpleCodeRuleDetail;
import org.hzero.boot.platform.code.repository.CodeRuleDetailRepository;
import org.hzero.boot.platform.code.utils.UUIDUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 编码规则默认构造器，用于获得编码
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 16:41
 */
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public class DefaultCodeRuleBuilder implements CodeRuleBuilder {
    private static final ThreadLocal<Boolean> sync = new ThreadLocal<>();
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private CodeRuleDetailRepository codeRuleDetailRepository;
    @Autowired
    private CodeRuleProperties codeRuleProperties;

    @Override
    public String generateCode(String ruleCode, Map<String, String> variableMap) {
        return this.generateCode(
                CodeConstants.Level.PLATFORM,
                BaseConstants.DEFAULT_TENANT_ID,
                ruleCode,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                variableMap
        );
    }

    @Override
    public List<String> generateCode(int quantity, String ruleCode, Map<String, String> variableMap) {
        return this.generateCode(quantity,
                CodeConstants.Level.PLATFORM,
                BaseConstants.DEFAULT_TENANT_ID,
                ruleCode,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                variableMap
        );
    }

    @Override
    public String generateCode(Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        CodeConstants.CodeRuleLevelCode.contains(levelCode);
        return this.generateCode(
                // 如果层级值为公司，生成公司级编码（为老方法做的兼容）
                CodeConstants.Level.TENANT,
                tenantId,
                ruleCode,
                levelCode,
                levelValue,
                variableMap
        );
    }

    @Override
    public List<String> generateCode(int quantity, Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        CodeConstants.CodeRuleLevelCode.contains(levelCode);
        return this.generateCode(quantity,
                // 如果层级值为公司，生成公司级编码（为老方法做的兼容）
                CodeConstants.Level.TENANT,
                tenantId,
                ruleCode,
                levelCode,
                levelValue,
                variableMap
        );
    }

    @Override
    public String generateCode(String level, Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        // 校验参数合法
        CodeRuleKey codeRuleKey = getCodeRuleKey(level, tenantId, ruleCode, levelCode, levelValue);
        List<CodeRuleDetail> codeRuleDetailList = getCodeRuleDetails(codeRuleKey);
        // 根据获取到的编码规则定义，生成编码规则
        return generateCodeRule(codeRuleDetailList, variableMap, codeRuleKey, true);
    }

    private List<CodeRuleDetail> getCodeRuleDetails(CodeRuleKey codeRuleKey) {
        List<CodeRuleDetail> codeRuleDetailList;
        do {
            codeRuleDetailList = codeRuleDetailRepository.listCodeRule(codeRuleKey);
        } while (CollectionUtils.isEmpty(codeRuleDetailList) && codeRuleKey.degrade());
        if (CollectionUtils.isEmpty(codeRuleDetailList)) {
            throw new CommonException(CodeConstants.ErrorCode.ERROR_CODE_RULE_NOT_FOUNT);
        }
        return codeRuleDetailList;
    }

    @Override
    public List<String> generateCode(int quantity, String level, Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        // 校验参数合法
        CodeRuleKey codeRuleKey = getCodeRuleKey(level, tenantId, ruleCode, levelCode, levelValue);
        List<CodeRuleDetail> codeRuleDetailList = getCodeRuleDetails(codeRuleKey);
        // 根据获取到的编码规则定义，生成编码规则
        List<String> generateCodeList = new ArrayList<>();
        for (int i = 1; i <= quantity; ++i) {
            generateCodeList.add(generateCodeRule(codeRuleDetailList, variableMap, codeRuleKey, i == quantity));
        }
        return generateCodeList;
    }

    @Override
    public long decryptSequence(String encrypt) {
        return EncryptionUtils.XOR.decrypt(codeRuleProperties.getEncryptKey(), encrypt);
    }

    private CodeRuleKey getCodeRuleKey(String level, Long tenantId, String ruleCode, String levelCode, String levelValue) {
        CodeConstants.Level.contains(level);
        CodeConstants.CodeRuleLevelCode.contains(levelCode);
        // 获取最佳匹配的编码规则
        return new CodeRuleKey(tenantId, ruleCode, level, levelCode, levelValue);
    }

    private String generateCodeRule(List<CodeRuleDetail> codeRuleDetailList, Map<String, String> variableMap, CodeRuleKey codeRuleKey, boolean syncNowIfNeed) {
        final Date now = new Date();
        StringBuilder ruleCodeBuilder = new StringBuilder();
        codeRuleDetailList.stream()
                .sorted(Comparator.comparingLong(CodeRuleDetail::getOrderSeq))
                .forEach(codeRuleDetail -> {
                    String fieldValue = null;
                    switch (codeRuleDetail.getFieldType()) {
                        case CodeConstants.FieldType.CONSTANT:
                            fieldValue = codeRuleDetail.getFieldValue();
                            break;
                        case CodeConstants.FieldType.DATE:
                            fieldValue = DateFormatUtils.format(now, codeRuleDetail.getDateMask());
                            break;
                        case CodeConstants.FieldType.SEQUENCE:
                            fieldValue = BaseConstants.Flag.YES.equals(codeRuleDetail.getEncryptedFlag())
                                    ? completionString(codeRuleDetail.getSeqLength(), EncryptionUtils.XOR.encrypt(codeRuleProperties.getEncryptKey(), getSequenceValue(codeRuleKey, codeRuleDetail, now, syncNowIfNeed)))
                                    : String.format("%0" + codeRuleDetail.getSeqLength() + "d", getSequenceValue(codeRuleKey, codeRuleDetail, now, syncNowIfNeed));
                            break;
                        case CodeConstants.FieldType.UUID:
                            Long seqLength = codeRuleDetail.getSeqLength();
                            if (seqLength == null) {
                                // -1时为默认生成32位UUID
                                seqLength = -1L;
                            }
                            fieldValue = UUIDUtils.getUUID(seqLength.intValue());
                            break;
                        case CodeConstants.FieldType.VARIABLE:
                            if (variableMap == null || !variableMap.containsKey(codeRuleDetail.getFieldValue())) {
                                fieldValue = "";
                                break;
                            }
                            fieldValue = variableMap.get(codeRuleDetail.getFieldValue());
                            break;
                        default:
                            break;
                    }
                    ruleCodeBuilder.append(fieldValue);
                });
        return ruleCodeBuilder.toString();
    }

    private static String completionString(Long size, String str) {
        if (size == null) {
            return str;
        }
        int currentSize = str != null ? str.length() : 0;
        if (currentSize < size) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size - currentSize; i++) {
                sb.append('0');
            }
            if (str != null) {
                sb.append(str);
            }
            return sb.toString();
        }
        return str;
    }

    /**
     * 得到序列的值
     *
     * @param codeRuleKey    编码规则信息
     * @param codeRuleDetail 编码规则明细对象
     * @param now            当前时间
     * @return 序列值
     */
    private long getSequenceValue(CodeRuleKey codeRuleKey, CodeRuleDetail codeRuleDetail, Date now, boolean syncNowIfNeed) {
        long sequence;
        boolean sync2Database = false;
        redisHelper.setCurrentDatabase(HZeroService.Platform.REDIS_DB);
        if (redisHelper.strGet(codeRuleKey.getSequenceKey()) == null) {
            sync2Database = true;
            if (codeRuleDetail.getCurrentValue() == null) {
                // 第一次使用序列，同时设置最后重置日期
                sequence = redisHelper.strIncrement(codeRuleKey.getSequenceKey(), codeRuleDetail.getStartValue());
            } else {
                // redis数据全部被清空，在redis里表现为第一次使用，但实际上不是第一次使用
                Long currentValue = codeRuleDetail.getCurrentValue()  + CodeConstants.Sequence.STEP_NUM;
                if (isResetSequence(codeRuleDetail.getResetFrequency(), codeRuleDetail.getResetDate(), now)) {
                    // 如果需要重置，应该从startValue开始递增
                       currentValue = codeRuleDetail.getStartValue();
                }
                sequence = redisHelper.strIncrement(codeRuleKey.getSequenceKey(),
                        currentValue);
                codeRuleDetail.setCurrentValue(sequence);
            }
            codeRuleDetail.setResetDate(now);
        } else {
            // 是否需要重置
            if (isResetSequence(codeRuleDetail.getResetFrequency(), codeRuleDetail.getResetDate(), now)) {
                sequence = resetSequence(codeRuleDetail, now, codeRuleKey);
                codeRuleDetail.setResetDate(now);
                // 表示将要更新
                sync2Database = true;
            } else {
                sequence = redisHelper.strIncrement(codeRuleKey.getSequenceKey(), CodeConstants.Sequence.STEP);
                if (codeRuleDetail.getCurrentValue() == null) {
                    // 出现该情况为异常情况，数据库数据丢失，同步数据库
                    sync2Database = true;
                } else if (sequence <= codeRuleDetail.getCurrentValue()) {
                    // 出现该情况为异常情况，Redis数据不一致，同步数据库数据到Redis
                    sync2Database = true;
                    CodeRuleDetail tempCoeRuleDetail =
                            codeRuleDetailRepository.selectByPrimaryKey(codeRuleKey.getTenantId(), codeRuleDetail.getRuleDetailId());
                    sequence = resetSequence(codeRuleKey.getSequenceKey(),
                            tempCoeRuleDetail.getCurrentValue() + CodeConstants.Sequence.STEP_NUM);
                    codeRuleDetail.setCurrentValue(sequence - 1);
                } else if (sequence % CodeConstants.Sequence.STEP_NUM == 0) {
                    // 达到步长，同步
                    sync2Database = true;
                }
            }
        }
        if (sync2Database) {
            setSyncIfAbsent();
        }
        if (needSync() && syncNowIfNeed) {
            recordSeqNumber(sequence, codeRuleDetail, codeRuleKey);
            sync.remove();
        }
        redisHelper.clearCurrentDatabase();
        return sequence;
    }

    private static void setSyncIfAbsent() {
        if (BooleanUtils.isNotTrue(sync.get())) {
            sync.set(true);
        }
    }

    private static boolean needSync() {
        return BooleanUtils.isTrue(sync.get());
    }

    /**
     * 判断是否需要更新序列数据库中的当前值，用以保证当Redis挂了后重新启用编码规则不至于出现重复现象
     *
     * @param sequenceValue  序列值
     * @param codeRuleDetail 编码规则明细
     * @param codeRuleKey    编码规则请求信息
     */
    private void recordSeqNumber(Long sequenceValue, CodeRuleDetail codeRuleDetail, CodeRuleKey codeRuleKey) {
        codeRuleDetail.setCurrentValue(sequenceValue);
        codeRuleDetailRepository.updateByPrimaryKey(codeRuleKey.getTenantId(), codeRuleDetail.setCodeRuleKey(codeRuleKey));
        redisHelper.hshPut(codeRuleKey.getKey(), String.valueOf(codeRuleDetail.getOrderSeq()),
                redisHelper.toJson(CommonConverter.beanConvert(SimpleCodeRuleDetail.class, codeRuleDetail)));
    }

    // 原子重置序列
    private long resetSequence(CodeRuleDetail codeRuleDetail, Date now, CodeRuleKey codeRuleKey) {
        int cnt = 5 * 60 * 1000 / 50;
        while (BooleanUtils.isNotTrue(redisHelper.strSetIfAbsent(codeRuleKey.getSequenceResetKey(), "lock"))) {
            try {
                if (cnt-- < 0) {
                    throw new CommonException("Code rule sequence reset waiting for lock timeout.");
                }
                Thread.sleep(50);
            } catch (Exception e) {
                throw new CommonException("An error occurred while the thread was sleeping.", e);
            }
        }
        try {
            redisHelper.setExpire(codeRuleKey.getSequenceResetKey(), 5, TimeUnit.MINUTES);
            String detailStr = redisHelper.hshGet(codeRuleKey.getKey(), String.valueOf(codeRuleDetail.getOrderSeq()));
            Assert.isTrue(StringUtils.hasText(detailStr), BaseConstants.ErrorCode.DATA_INVALID);
            CodeRuleDetail detail = redisHelper.fromJson(detailStr, CodeRuleDetail.class);
            if (Objects.equals(codeRuleDetail.getResetDate(), detail.getResetDate())) {
                detail.setResetDate(now);
                redisHelper.hshPut(codeRuleKey.getKey(), String.valueOf(codeRuleDetail.getOrderSeq()), redisHelper.toJson(detail));
                redisHelper.strSet(codeRuleKey.getSequenceKey(), String.valueOf(codeRuleDetail.getStartValue()));
                return codeRuleDetail.getStartValue();
            }
            return redisHelper.strIncrement(codeRuleKey.getSequenceKey(), 1L);
        } finally {
            redisHelper.delKey(codeRuleKey.getSequenceResetKey());
        }
    }

    /**
     * 重置序列，并返回初始值
     *
     * @param key        缓存key
     * @param startValue 开始值
     */
    private Long resetSequence(String key, Long startValue) {
        redisHelper.delKey(key);
        return redisHelper.strIncrement(key, startValue);
    }

    /**
     * 判断序列是否需要重置
     *
     * @param resetType    重置类型
     * @param lastRestDate 最后一次重置时间
     * @param now          现在时间
     * @return boolean
     */
    private boolean isResetSequence(String resetType, Date lastRestDate, Date now) {
        if (!CodeConstants.ResetFrequency.NEVER.equalsIgnoreCase(resetType) && lastRestDate == null) {
            return true;
        } else if (CodeConstants.ResetFrequency.YEAR.equalsIgnoreCase(resetType)) {
            return !DateFormatUtils.format(lastRestDate, "yyyy").equals(DateFormatUtils.format(now, "yyyy"));
        } else if (CodeConstants.ResetFrequency.QUARTER.equalsIgnoreCase(resetType)) {
            Calendar resetDate = Calendar.getInstance();
            resetDate.setTime(lastRestDate);
            Calendar nowDate = Calendar.getInstance();
            nowDate.setTime(now);
            return resetDate.get(Calendar.YEAR) != nowDate.get(Calendar.YEAR)
                    || (resetDate.get(Calendar.MONTH) / 3 != nowDate.get(Calendar.MONTH) / 3);
        } else if (CodeConstants.ResetFrequency.MONTH.equalsIgnoreCase(resetType)) {
            return !DateFormatUtils.format(lastRestDate, "MM").equals(DateFormatUtils.format(now, "MM"));
        } else if (CodeConstants.ResetFrequency.DAY.equalsIgnoreCase(resetType)) {
            return !DateFormatUtils.format(lastRestDate, "dd").equals(DateFormatUtils.format(now, "dd"));
        }
        return false;
    }

    @Override
    public String generatePlatformLevelCode(String ruleCode, Map<String, String> variableMap) {
        return this.generateCode(
                CodeConstants.Level.PLATFORM,
                BaseConstants.DEFAULT_TENANT_ID,
                ruleCode,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                CodeConstants.CodeRuleLevelCode.GLOBAL,
                variableMap
        );
    }

    @Override
    public String generateTenantLevelCode(Long tenantId, String ruleCode, String levelCode, String levelValue, Map<String, String> variableMap) {
        return this.generateCode(
                CodeConstants.Level.TENANT,
                tenantId,
                ruleCode,
                levelCode,
                levelValue,
                variableMap
        );
    }

    public static class CodeRuleKey {

        private Long tenantId;
        private String ruleCode;
        private String level;
        private String levelCode;
        private String levelValue;
        private String key;
        private String usedKey;
        private String sequenceKey;
        private String failFastKey;
        private CodeRuleKey previous;

        public CodeRuleKey() {
        }

        public CodeRuleKey(Long tenantId, String ruleCode, String level, String levelCode, String levelValue) {
            if (CodeConstants.Level.PLATFORM.equals(level)) {
                tenantId = BaseConstants.DEFAULT_TENANT_ID;
                levelCode = CodeConstants.CodeRuleLevelCode.GLOBAL;
                levelValue = CodeConstants.CodeRuleLevelCode.GLOBAL;
            } else if (CodeConstants.Level.COMPANY.equals(level) || CodeConstants.Level.CUSTOM.equals(level)) {
                // 用公司去获取租户编码
                level = CodeConstants.Level.TENANT;
                if (CodeConstants.CodeRuleLevelCode.GLOBAL.equals(levelCode)) {
                    levelValue = CodeConstants.CodeRuleLevelCode.GLOBAL;
                }
            }
            this.tenantId = tenantId;
            this.ruleCode = ruleCode;
            this.level = level;
            this.levelCode = levelCode;
            this.levelValue = levelValue;
        }

        /**
         * @return 编码规则是否能够降级匹配
         */
        private boolean degradable() {
            return !(BaseConstants.DEFAULT_TENANT_ID.equals(tenantId)
                    && CodeConstants.CodeRuleLevelCode.GLOBAL.equalsIgnoreCase(levelCode)
                    && CodeConstants.CodeRuleLevelCode.GLOBAL.equalsIgnoreCase(levelValue));
        }

        /**
         * 降级编码规则
         */
        private boolean degrade() {
            if (!degradable()) {
                return false;
            }
            if (previous == null) {
                previous = new CodeRuleKey(tenantId, ruleCode, level, levelCode, levelValue);
            }
            key = null;
            failFastKey = null;
            // 公司级编码规则无法找到 -> 查询租户级全局编码规则
            if (CodeConstants.Level.COMPANY.equals(level)) {
                level = CodeConstants.Level.TENANT;
                levelCode = CodeConstants.CodeRuleLevelCode.GLOBAL;
                levelValue = CodeConstants.CodeRuleLevelCode.GLOBAL;
            } else /* 公司级编码规则无法找到 -> 查询租户级全局编码规则 0.8.0.RELEASE逻辑兼容 */
                if (CodeConstants.Level.TENANT.equals(level) && (
                        CodeConstants.CodeRuleLevelCode.COMPANY.equals(levelCode) || CodeConstants.CodeRuleLevelCode.CUSTOM.equals(levelCode)
                )) {
                    levelCode = CodeConstants.CodeRuleLevelCode.GLOBAL;
                    levelValue = CodeConstants.CodeRuleLevelCode.GLOBAL;
                } else /* 租户级编码规则未找到 -> 查找平台级全局编码规则 */ {
                    level = CodeConstants.Level.PLATFORM;
                    tenantId = BaseConstants.DEFAULT_TENANT_ID;
                    levelCode = CodeConstants.CodeRuleLevelCode.GLOBAL;
                    levelValue = CodeConstants.CodeRuleLevelCode.GLOBAL;
                }
            return true;
        }

        public String getKey() {
            if (key != null) {
                return key;
            }
            if (previous == null) {
                key = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue;
            } else {
                key = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue
                        + "-" + previous.tenantId
                        + "." + previous.level
                        + "." + previous.levelCode
                        + "." + previous.levelValue;
            }
            return key;
        }

        public String getUsedKey() {
            if (usedKey != null) {
                return usedKey;
            }
            usedKey = CodeConstants.CacheKey.CODE_RULE_KEY
                    + ":used"
                    + ":" + level
                    + "." + tenantId
                    + "." + ruleCode
                    + "." + levelCode
                    + "." + levelValue;
            return usedKey;
        }

        public String getSequenceKey() {
            if (sequenceKey != null) {
                return sequenceKey;
            }
            if (previous == null) {
                sequenceKey = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":sequence"
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue;
            } else {
                sequenceKey = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":sequence"
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue
                        + "-" + previous.tenantId
                        + "." + previous.level
                        + "." + previous.levelCode
                        + "." + previous.levelValue;
            }
            return sequenceKey;
        }

        public String getSequenceResetKey() {
            return getSequenceKey() + ".reset-lock";
        }

        public String getFailFastKey() {
            if (failFastKey != null) {
                return failFastKey;
            }
            if (previous == null) {
                failFastKey = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":fail-fast"
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue;
            } else {
                failFastKey = CodeConstants.CacheKey.CODE_RULE_KEY
                        + ":fail-fast"
                        + ":" + level
                        + "." + tenantId
                        + "." + ruleCode
                        + "." + levelCode
                        + "." + levelValue
                        + "-" + previous.tenantId
                        + "." + previous.level
                        + "." + previous.levelCode
                        + "." + previous.levelValue;
            }
            return failFastKey;
        }

        public Long getPreviousTenantId() {
            return previous == null ? tenantId : previous.tenantId;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public CodeRuleKey setTenantId(Long tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public String getRuleCode() {
            return ruleCode;
        }

        public CodeRuleKey setRuleCode(String ruleCode) {
            this.ruleCode = ruleCode;
            return this;
        }

        public String getLevel() {
            return level;
        }

        public CodeRuleKey setLevel(String level) {
            this.level = level;
            return this;
        }

        public String getLevelCode() {
            return levelCode;
        }

        public CodeRuleKey setLevelCode(String levelCode) {
            this.levelCode = levelCode;
            return this;
        }

        public String getLevelValue() {
            return levelValue;
        }

        public CodeRuleKey setLevelValue(String levelValue) {
            this.levelValue = levelValue;
            return this;
        }

        public CodeRuleKey getPrevious() {
            return previous;
        }

        public String getPreviousLevel() {
            if (previous != null) {
                return previous.getLevel();
            }
            return null;
        }

        public String getPreviousLevelCode() {
            if (previous != null) {
                return previous.getLevelCode();
            }
            return null;
        }

        public String getPreviousLevelValue() {
            if (previous != null) {
                return (CodeConstants.CodeRuleLevelCode.COMPANY.equals(previous.getLevelCode()) || CodeConstants.CodeRuleLevelCode.CUSTOM.equals(previous.getLevelCode())) ?
                        previous.getLevelValue() : String.valueOf(previous.getTenantId());
            }
            return null;
        }
    }
}
