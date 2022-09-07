/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.choerodon.mybatis.domain;

import java.util.Properties;

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.code.IdentityDialect;
import io.choerodon.mybatis.code.Style;
import io.choerodon.mybatis.code.DbType;
import io.choerodon.mybatis.util.SimpleTypeUtil;
import io.choerodon.mybatis.util.StringUtil;


/**
 * 通用Mapper属性配置
 *
 * @author liuzh
 */
public class Config {
    private String uuid;
    private String identity;
    private boolean before;
    private String seqFormat;
    private String catalog;
    private String schema;
    //校验调用Example方法时，Condition(entityClass)和Mapper<EntityClass>是否一致
    private boolean checkExampleEntityClass;
    //使用简单类型
    private boolean useSimpleType;
    //数据库类型
    private DbType dbType;
    /**
     * 是否支持方法上的注解，默认false
     */
    private boolean enableMethodAnnotation;
    /**
     * 对于一般的getAllIfColumnNode，是否判断!=''，默认不判断
     */
    private boolean notEmpty = false;

    /**
     * 字段转换风格，默认驼峰转下划线
     */
    private Style style;
    /**
     * 安全删除，开启后，不允许删全表，如 delete from table
     */
    private boolean safeDelete;

    /**
     * 获取SelectKey的Order
     *
     * @return boolean
     */
    public boolean isBefore() {
        return before;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    /**
     * 主键自增回写方法执行顺序,默认AFTER,可选值为(before|AFTER)
     *
     * @param order order
     */
    public void setOrder(String order) {
        this.before = "before".equalsIgnoreCase(order);
    }

    public String getCatalog() {
        return catalog;
    }

    /**
     * 设置全局的catalog,默认为空，如果设置了值，操作表时的sql会是catalog.tablename
     *
     * @param catalog catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * 获取主键自增回写SQL
     *
     * @return String
     */
    public String getIdentity() {
        if (StringUtil.isNotEmpty(this.identity)) {
            return this.identity;
        }
        //针对mysql的默认值
        return IdentityDialect.MYSQL.getIdentityRetrievalStatement();
    }

    /**
     * 主键自增回写方法,默认值MYSQL,详细说明请看文档
     *
     * @param identity identity
     */
    public void setIdentity(String identity) {
        IdentityDialect identityDialect = IdentityDialect.getDatabaseDialect(identity);
        if (identityDialect != null) {
            this.identity = identityDialect.getIdentityRetrievalStatement();
        } else {
            this.identity = identity;
        }
    }

    public String getSchema() {
        return schema;
    }

    /**
     * 设置全局的schema,默认为空，如果设置了值，操作表时的sql会是schema.tablename
     * <br>如果同时设置了catalog,优先使用catalog.tablename
     *
     * @param schema schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * 获取序列格式化模板
     *
     * @return String
     */
    public String getSeqFormat() {
        if (StringUtil.isNotEmpty(this.seqFormat)) {
            return this.seqFormat;
        }
        return "{0}.nextval";
    }

    /**
     * 序列的获取规则,使用{num}格式化参数，默认值为{0}.nextval，针对Oracle
     * <br>可选参数一共3个，对应0,1,2,3分别为SequenceName，ColumnName, PropertyName，TableName
     *
     * @param seqFormat seqFormat
     */
    public void setSeqFormat(String seqFormat) {
        this.seqFormat = seqFormat;
    }

    /**
     * 获取UUID生成规则
     *
     * @return String
     */
    public String getUuid() {
        if (StringUtil.isNotEmpty(this.uuid)) {
            return this.uuid;
        }
        return "@java.util.UUID@randomUUID().toString().replace(\"-\", \"\")";
    }

    /**
     * 设置UUID生成策略
     * <br>配置UUID生成策略需要使用OGNL表达式
     * <br>默认值32位长度:@java.util.uuid@randomUUID().toString().replace("-", "")
     *
     * @param uuid uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isNotEmpty() {
        return notEmpty;
    }

    public void setNotEmpty(boolean notEmpty) {
        this.notEmpty = notEmpty;
    }

    public Style getStyle() {
        return this.style == null ? Style.CAMELHUMP : this.style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isEnableMethodAnnotation() {
        return enableMethodAnnotation;
    }

    public void setEnableMethodAnnotation(boolean enableMethodAnnotation) {
        this.enableMethodAnnotation = enableMethodAnnotation;
    }

    public boolean isCheckExampleEntityClass() {
        return checkExampleEntityClass;
    }

    public void setCheckExampleEntityClass(boolean checkExampleEntityClass) {
        this.checkExampleEntityClass = checkExampleEntityClass;
    }

    public boolean isUseSimpleType() {
        return useSimpleType;
    }

    public void setUseSimpleType(boolean useSimpleType) {
        this.useSimpleType = useSimpleType;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean isSafeDelete() {
        return safeDelete;
    }

    public Config setSafeDelete(boolean safeDelete) {
        this.safeDelete = safeDelete;
        return this;
    }

    /**
     * 获取表前缀，带catalog或schema
     *
     * @return String
     */
    public String getPrefix() {
        if (StringUtil.isNotEmpty(this.catalog)) {
            return this.catalog;
        }
        if (StringUtil.isNotEmpty(this.schema)) {
            return this.schema;
        }
        return "";
    }

    /**
     * 配置属性
     *
     * @param properties properties
     */
    public void setProperties(Properties properties) {
        if (properties == null) {
            //默认驼峰
            this.style = Style.CAMELHUMP;
            return;
        }

        String dataBaseType = properties.getProperty("dbType");
        if (StringUtil.isNotEmpty(dataBaseType)) {
            setDbType(DbType.getByValue(dataBaseType));
        }

        String newUuid = properties.getProperty("uuid");
        if (StringUtil.isNotEmpty(newUuid)) {
            setUuid(newUuid);
        }
        String newIdentity = properties.getProperty("identity");
        if (StringUtil.isNotEmpty(newIdentity)) {
            setIdentity(newIdentity);
        }
        String newSeqFormat = properties.getProperty("seqFormat");
        if (StringUtil.isNotEmpty(newSeqFormat)) {
            setSeqFormat(newSeqFormat);
        }
        String newCatalog = properties.getProperty("catalog");
        if (StringUtil.isNotEmpty(newCatalog)) {
            setCatalog(newCatalog);
        }
        String newSchema = properties.getProperty("schema");
        if (StringUtil.isNotEmpty(newSchema)) {
            setSchema(newSchema);
        }
        String order = properties.getProperty("ORDER");
        if (StringUtil.isNotEmpty(order)) {
            setOrder(order);
        }
        String newNotEmpty = properties.getProperty("notEmpty");
        if (StringUtil.isNotEmpty(newNotEmpty)) {
            this.notEmpty = newNotEmpty.equalsIgnoreCase("TRUE");
        }
        String newEnableMethodAnnotation = properties.getProperty("enableMethodAnnotation");
        if (StringUtil.isNotEmpty(newEnableMethodAnnotation)) {
            this.enableMethodAnnotation = newEnableMethodAnnotation.equalsIgnoreCase("TRUE");
        }
        String checkExampleStr = properties.getProperty("checkExampleEntityClass");
        if (StringUtil.isNotEmpty(checkExampleStr)) {
            this.checkExampleEntityClass = checkExampleStr.equalsIgnoreCase("TRUE");
        }
        String useSimpleTypeStr = properties.getProperty("useSimpleType");
        if (StringUtil.isNotEmpty(useSimpleTypeStr)) {
            this.useSimpleType = useSimpleTypeStr.equalsIgnoreCase("TRUE");
        }
        //注册新的基本类型，以逗号隔开，使用全限定类名
        registerNewType(properties);
    }

    private void registerNewType(Properties properties) {
        String simpleTypes = properties.getProperty("simpleTypes");
        if (StringUtil.isNotEmpty(simpleTypes)) {
            SimpleTypeUtil.registerSimpleType(simpleTypes);
        }
        String styleStr = properties.getProperty("style");
        if (StringUtil.isNotEmpty(styleStr)) {
            try {
                this.style = Style.valueOf(styleStr);
            } catch (IllegalArgumentException e) {
                throw new MapperException(styleStr + "不是合法的Style值!");
            }
        } else {
            //默认驼峰
            this.style = Style.CAMELHUMP;
        }
    }
}
