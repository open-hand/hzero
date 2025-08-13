package org.hzero.boot.platform.code.constant;

import org.hzero.common.HZeroService;

import io.choerodon.core.exception.CommonException;

/**
 * <p>
 * 编码规则常量
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/09/07 13:53
 */
public class CodeConstants {
    private CodeConstants() {
    }

    /**
     * 服务简称
     */
    public static final String APP_CODE = HZeroService.Platform.CODE;

    /**
     * 用于组装生成编码规则是否使用过的key
     */
    public static final String USED_FLAG = "USED";

    /**
     * 判断编码规则明细是否需要更新标识
     */
    public static final String UPDATE_FLAG = "flag";

    /**
     * 在数据库中查询无值时 设置标识
     */
    public static final String FAIL_FAST = "fail_fast";
    /**
     * 快速失败标识 过期时间
     */
    public static final long FORBIDDEN_TIME = 3600;
    /**
     * 编码规则配置前缀
     */
    public static final String CODE_PREFIX = "hzero.code-rule";

    public static class ErrorCode {
        private ErrorCode() {
        }

        /**
         * 编码规则未找到
         */
        public static final String ERROR_CODE_RULE_NOT_FOUNT = "hpfm.error.code_rule.not_fount";

        /**
         * 编码规则明细未找到
         */
        public static final String ERROR_CODE_RULE_DETAIL_NOT_FOUNT = "hpfm.error.code_rule_detail.not_fount";

        /**
         * 编码规则明细更新失败
         */
        public static final String ERROR_CODE_RULE_DETAIL_UPDATE_FAILED = "hpfm.error.code_rule_detail.update_failed";

        /**
         * 编码规则分配更新失败
         */
        public static final String ERROR_CODE_RULE_DIST_UPDATE_FAILED = "hpfm.error.code_rule_dist.update_failed";
        /**
         * 编码规则客户端请求失败
         */
        public static final String ERROR_CODE_RULE_CLIENT_REQUEST = "hpfm.error.code_rule_client.request";
        /**
         * 编码规则被禁用
         */
        public static final String ERROR_CODE_RULE_DISABLED = "hpfm.error.code_rule.disabled";
    }

    public static class CacheKey {
        private CacheKey() {
        }

        /**
         * 编码规则
         */
        public static final String CODE_RULE_KEY = CodeConstants.APP_CODE + ":code-rule";
    }

    /**
     * 序列
     */
    public static class Sequence {
        private Sequence() {
        }

        /**
         * 自增步长
         */
        public static final Long STEP = 1L;

        /**
         * 记录频率
         */
        public static final Long STEP_NUM = 100L;
    }

    /**
     * 重置频率
     */
    public static class ResetFrequency {
        private ResetFrequency() {
        }

        /**
         * 从不
         */
        public static final String NEVER = "NEVER";

        /**
         * 每年
         */
        public static final String YEAR = "YEAR";

        /**
         * 每季
         */
        public static final String QUARTER = "QUARTER";

        /**
         * 每月
         */
        public static final String MONTH = "MONTH";

        /**
         * 每天
         */
        public static final String DAY = "DAY";
    }

    /**
     * 应用维度
     */
    public static class Level {
        private Level() {
        }

        /**
         * 应用维度-平台级
         */
        public static final String PLATFORM = "P";
        /**
         * 应用维度-租户级
         */
        public static final String TENANT = "T";
        /**
         * 应用维度-公司级
         */
        public static final String COMPANY = "C";
        /**
         * 应用维度-自定义
         */
        public static final String CUSTOM = "CT";

        public static void contains(String level) {
            if (TENANT.equals(level) || PLATFORM.equals(level) || COMPANY.equals(level) || CUSTOM.equals(level)) {
                return;
            }
            throw new CommonException("level : " + level + " , must be in [T, P, C, CT]");
        }
    }

    /**
     * 编码规则应用层级
     */
    public static class CodeRuleLevelCode {
        private CodeRuleLevelCode() {
        }

        /**
         * 全局级
         */
        public static final String GLOBAL = "GLOBAL";

        /**
         * 公司
         */
        public static final String COMPANY = "COM";
        /**
         * 公司
         */
        public static final String CUSTOM = "CUSTOM";

        /**
         * 判断字符串是不是合法的应用层级
         *
         * @param levelCode 应用层级
         */
        public static void contains(String levelCode) {
            if (GLOBAL.equals(levelCode) || COMPANY.equals(levelCode) || CUSTOM.equals(levelCode)) {
                return;
            }
            throw new CommonException("levelCode : " + levelCode + " , must be in [GLOBAL, COM, CUSTOM]");
        }
    }

    /**
     * 编码规则段类型
     */
    public static class FieldType {
        private FieldType() {
        }

        /**
         * 序列
         */
        public static final String SEQUENCE = "SEQUENCE";

        /**
         * 常量
         */
        public static final String CONSTANT = "CONSTANT";

        /**
         * 日期
         */
        public static final String DATE = "DATE";

        /**
         * 变量
         */
        public static final String VARIABLE = "VARIABLE";

        /**
         * uuid
         */
        public static final String UUID = "UUID";
    }
}
