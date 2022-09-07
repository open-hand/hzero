package org.hzero.mybatis.domian;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 加密实体内容
 * </p>
 *
 * @author qingsheng.chen 2018/9/13 星期四 10:47
 */
public class SecurityTokenEntity {
    private String applicationName;
    private String contextPath;
    private String className;
    private Map<String, Object> pkValue;

    public String getApplicationName() {
        return applicationName;
    }

    public SecurityTokenEntity setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public String getContextPath() {
        return contextPath;
    }

    public SecurityTokenEntity setContextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public SecurityTokenEntity setClassName(String className) {
        this.className = className;
        return this;
    }

    public Map<String, Object> getPkValue() {
        return pkValue;
    }

    public SecurityTokenEntity setPkValue(Map<String, Object> pkValue) {
        this.pkValue = pkValue;
        return this;
    }

    public SecurityTokenEntity addPkValue(String field, Object value) {
        if (this.pkValue == null) {
            this.pkValue = new HashMap<>(2);
        }
        this.pkValue.put(field, value);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SecurityTokenEntity)) {
            return false;
        }

        SecurityTokenEntity that = (SecurityTokenEntity) o;

        if (!applicationName.equals(that.applicationName)) {
            return false;
        }
        if (!className.equals(that.className)) {
            return false;
        }
        return pkValue != null ? pkValue.equals(that.pkValue) : that.pkValue == null;
    }

    @Override
    public int hashCode() {
        int result = applicationName.hashCode();
        result = 31 * result + className.hashCode();
        result = 31 * result + (pkValue != null ? pkValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SecurityTokenEntity{" +
                "applicationName='" + applicationName + '\'' +
                ", className='" + className + '\'' +
                ", pkValue=" + pkValue +
                '}';
    }
}
