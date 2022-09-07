package org.hzero.boot.platform.lov.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 值集组件属性
 *
 * @author gaokuo.dai@hand-china.com 2018年8月30日下午2:21:37
 */
@ConfigurationProperties(prefix = LovProperties.LOV_PROPERTIES_PREFIX)
public class LovProperties {

    public static final String LOV_PROPERTIES_PREFIX = "hzero.lov";

    private Sql sql = new Sql();
    private Value value = new Value();

    private TranslateSql translateSql = new TranslateSql();

    /**
     * Sql值集处理支持
     */
    public static class Sql {

        public static final String LOV_SQL_PROPERTIES_PREFIX = "sql";

        private boolean enabled = true;
        private boolean publicApiEnabled = false;

        /**
         * @return 是否启用, 默认true
         */
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isPublicApiEnabled() {
            return publicApiEnabled;
        }

        public void setPublicApiEnabled(boolean publicApiEnabled) {
            this.publicApiEnabled = publicApiEnabled;
        }
    }

    /**
     * 值集值处理支持
     */
    public static class Value {

        public static final String LOV_VALUE_PROPERTIES_PREFIX = "value";

        private boolean enabled = true;

        /**
         * @return 是否启用, 默认true
         */
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class TranslateSql {

        /**
         * 翻译sql是否直接在当前服务执行
         */
        private boolean local = false;

        public boolean isLocal() {
            return local;
        }

        public void setLocal(boolean local) {
            this.local = local;
        }
    }

    public Sql getSql() {
        return sql;
    }

    public Value getValue() {
        return value;
    }

    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public TranslateSql getTranslateSql() {
        return translateSql;
    }

    public void setTranslateSql(TranslateSql translateSql) {
        this.translateSql = translateSql;
    }
}
