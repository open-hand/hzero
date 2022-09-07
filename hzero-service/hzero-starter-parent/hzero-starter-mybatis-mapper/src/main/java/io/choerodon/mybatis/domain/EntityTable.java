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

import io.choerodon.mybatis.MapperException;
import io.choerodon.mybatis.util.StringUtil;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 数据库表
 *
 * @author liuzh
 */
public class EntityTable {
    // 别名初始值
    private static final char ALIAS_START = 'A';
    // 别名序号
    private int currentAliasCharIndex = 0;
    /**
     * 属性和列对应
     */
    protected Map<String, EntityColumn> propertyMap;
    private String name;
    private String catalog;
    private String schema;
    private String orderByClause;
    private String baseSelect;
    private boolean multiLanguage;
    private boolean multiLanguageUnique;
    private String multiLanguageTableName;
    /**
     * 实体类 => 全部列属性
     */
    private Set<EntityColumn> entityClassColumns;
    /**
     * 实体类 =》 非列字段
     */
    private Set<EntityColumn> entityClassTransientColumns;
    /**
     * 实体类 => 主键信息
     */
    private Set<EntityColumn> entityClassPkColumns;
    private Set<EntityColumn> multiLanguageColumns;
    /**
     * 实体类 =》 唯一字段
     */
    private Set<EntityColumn> uniqueColumns;
    /**
     * useGenerator包含多列的时候需要用到
     */
    private List<String> keyProperties;
    private List<String> keyColumns;
    /**
     * resultMap对象
     */
    private ResultMap resultMap;
    /**
     * 类
     */
    private Class<?> entityClass;

    /**
     * 属性列表
     */
    private Set<EntityField> fieldSet;

    /**
     * 是否数据加密
     */
    private boolean dataSecurity;
    /**
     * 数据加密的列
     */
    private Set<EntityColumn> dataSecurityColumns;

    public EntityTable(Class<?> entityClass) {
        this.entityClass = entityClass;
        createAlias(entityClass.getCanonicalName());
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * Security Token 字段
     */
    private Set<EntityField> securityTokenFieldSet = new HashSet<>();

    /**
     * Join映射
     */
    private Map<String, EntityColumn> joinMapping = new HashMap<>();
    private Map<String, String> aliasMapping = new HashMap<>();
    /**
     * Where列
     */
    private List<EntityColumn> whereColumns = new ArrayList<>();
    private Set<EntityColumn> sortColumns = new LinkedHashSet<>();

    /**
     * 根据表设置EntityTable的name，catalog，schema
     *
     * @param table table
     */
    public void setTable(Table table) {
        this.name = table.name();
        this.catalog = table.catalog();
        this.schema = table.schema();
    }

    public String getMultiLanguageTableName() {
        return multiLanguageTableName;
    }

    public void setMultiLanguageTableName(String multiLanguageTableName) {
        this.multiLanguageTableName = multiLanguageTableName;
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    public boolean isMultiLanguageUnique() {
        return multiLanguageUnique;
    }

    public EntityTable setMultiLanguageUnique(boolean multiLanguageUnique) {
        this.multiLanguageUnique = multiLanguageUnique;
        return this;
    }

    public Set<EntityColumn> getMultiLanguageColumns() {
        return multiLanguageColumns;
    }

    public void setMultiLanguageColumns(Set<EntityColumn> multiLanguageColumns) {
        this.multiLanguageColumns = multiLanguageColumns;
    }

    public void setKeyColumns(List<String> keyColumns) {
        this.keyColumns = keyColumns;
    }

    /**
     * 设置keyColumns
     *
     * @param keyColumn keyColumn
     */
    public void setKeyColumns(String keyColumn) {
        if (this.keyColumns == null) {
            this.keyColumns = new ArrayList<>();
            this.keyColumns.add(keyColumn);
        } else {
            this.keyColumns.add(keyColumn);
        }
    }

    public void setKeyProperties(List<String> keyProperties) {
        this.keyProperties = keyProperties;
    }

    /**
     * 设置keyProperty
     *
     * @param keyProperty keyProperty
     */
    public void setKeyProperties(String keyProperty) {
        if (this.keyProperties == null) {
            this.keyProperties = new ArrayList<>();
            this.keyProperties.add(keyProperty);
        } else {
            this.keyProperties.add(keyProperty);
        }
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getBaseSelect() {
        return baseSelect;
    }

    public void setBaseSelect(String baseSelect) {
        this.baseSelect = baseSelect;
    }

    /**
     * 获得前缀
     *
     * @return String
     */
    public String getPrefix() {
        if (StringUtil.isNotEmpty(catalog)) {
            return catalog;
        }
        if (StringUtil.isNotEmpty(schema)) {
            return schema;
        }
        return "";
    }

    public Set<EntityColumn> getEntityClassColumns() {
        return entityClassColumns;
    }

    public void setEntityClassColumns(Set<EntityColumn> entityClassColumns) {
        this.entityClassColumns = entityClassColumns;
    }

    public Set<EntityColumn> getEntityClassPkColumns() {
        return entityClassPkColumns;
    }

    public void setEntityClassPkColumns(Set<EntityColumn> entityClassPkColumns) {
        this.entityClassPkColumns = entityClassPkColumns;
    }

    public Set<EntityColumn> getEntityClassTransientColumns() {
        return entityClassTransientColumns;
    }

    public EntityTable setEntityClassTransientColumns(Set<EntityColumn> entityClassTransientColumns) {
        this.entityClassTransientColumns = entityClassTransientColumns;
        return this;
    }

    /**
     * 返回keyProperties
     *
     * @return String[]
     */
    public String[] getKeyProperties() {
        if (keyProperties != null && !keyProperties.isEmpty()) {
            return keyProperties.toArray(new String[]{});
        }
        return new String[]{};
    }

    /**
     * 返回keyColumns
     *
     * @return String[]
     */
    public String[] getKeyColumns() {
        if (keyColumns != null && !keyColumns.isEmpty()) {
            return keyColumns.toArray(new String[]{});
        }
        return new String[]{};
    }


    /**
     * 生成当前实体的resultMap对象
     *
     * @param configuration configuration
     * @return ResultMap
     */
    public ResultMap getResultMap(Configuration configuration) {
        if (this.resultMap != null) {
            return this.resultMap;
        }
        if (entityClassColumns == null || entityClassColumns.isEmpty()) {
            return null;
        }
        List<ResultMapping> resultMappings = new ArrayList<>();
        for (EntityColumn entityColumn : entityClassColumns) {
            ResultMapping.Builder builder =
                    new ResultMapping.Builder(configuration, entityColumn.getProperty(),
                            entityColumn.getColumn(), entityColumn.getJavaType());
            if (entityColumn.getJdbcType() != null) {
                builder.jdbcType(entityColumn.getJdbcType());
            }
            if (entityColumn.getTypeHandler() != null) {
                try {
                    builder.typeHandler(entityColumn.getTypeHandler().newInstance());
                } catch (Exception e) {
                    throw new MapperException(e);
                }
            }
            List<ResultFlag> flags = new ArrayList<>();
            if (entityColumn.isId()) {
                flags.add(ResultFlag.ID);
            }
            builder.flags(flags);
            resultMappings.add(builder.build());
        }
        ResultMap.Builder builder = new ResultMap.Builder(configuration, "BaseMapperResultMap",
                this.entityClass, resultMappings, true);
        this.resultMap = builder.build();
        return this.resultMap;
    }

    /**
     * 初始化 - Condition 会使用
     */
    public void initPropertyMap() {
        propertyMap = new HashMap<>(getEntityClassColumns().size());
        for (EntityColumn column : getEntityClassColumns()) {
            propertyMap.put(column.getProperty(), column);
        }
    }

    public Map<String, EntityColumn> getPropertyMap() {
        return propertyMap;
    }

    public Set<EntityField> getFieldSet() {
        return fieldSet;
    }

    public void appendField(EntityField field) {
        if (fieldSet == null) {
            fieldSet = new HashSet<>(16);
        }
        fieldSet.add(field);
    }

    public boolean isDataSecurity() {
        return dataSecurity;
    }

    public EntityTable setDataSecurity(boolean dataSecurity) {
        this.dataSecurity = dataSecurity;
        return this;
    }

    public Set<EntityColumn> getDataSecurityColumns() {
        return dataSecurityColumns;
    }

    public EntityTable setDataSecurityColumns(Set<EntityColumn> dataSecurityColumns) {
        this.dataSecurityColumns = dataSecurityColumns;
        return this;
    }

    public void addDataSecurityColumns(EntityColumn dataSecurityColumn) {
        if (this.dataSecurityColumns == null) {
            this.dataSecurityColumns = new HashSet<>();
        }
        this.dataSecurityColumns.add(dataSecurityColumn);
    }

    public Set<EntityColumn> getUniqueColumns() {
        return uniqueColumns;
    }

    public Set<EntityColumn> getUniqueColumns(String constraintName) {
        Set<EntityColumn> uniqueColumns = getUniqueColumns();
        if (CollectionUtils.isEmpty(uniqueColumns)) {
            return Collections.emptySet();
        }
        return uniqueColumns.stream()
                .filter(each -> each.getConstraintNames().contains(constraintName))
                .collect(Collectors.toSet());
    }

    public EntityTable setUniqueColumns(Set<EntityColumn> uniqueColumns) {
        this.uniqueColumns = uniqueColumns;
        return this;
    }

    public void addUniqueColumn(EntityColumn entityColumn) {
        if (this.uniqueColumns == null) {
            this.uniqueColumns = new HashSet<>();
        }
        this.uniqueColumns.add(entityColumn);
    }

    public Set<EntityField> getSecurityTokenFieldSet() {
        return securityTokenFieldSet;
    }

    public EntityTable setSecurityTokenFieldSet(Set<EntityField> securityTokenFieldSet) {
        this.securityTokenFieldSet = securityTokenFieldSet;
        return this;
    }

    public EntityTable addSecurityTokenField(EntityField securityTokenField) {
        this.securityTokenFieldSet.add(securityTokenField);
        return this;
    }

    public EntityColumn findColumnByProperty(String field) {
        return entityClassColumns.stream()
                .filter(item -> Objects.equals(item.getField().getName(), field))
                .findFirst()
                .orElse(entityClassTransientColumns.stream()
                        .filter(item -> Objects.equals(item.getField().getName(), field))
                        .findFirst()
                        .orElse(null));
    }

    public Map<String, EntityColumn> getJoinMapping() {
        return joinMapping;
    }

    public EntityTable setJoinMapping(Map<String, EntityColumn> joinMapping) {
        this.joinMapping = joinMapping;
        return this;
    }

    public String getAlias() {
        return getAlias(null);
    }

    public String getAlias(String key) {
        return key == null ? getAliasNotNull(entityClass.getCanonicalName()) : getAliasNotNull(key);
    }

    public String getAliasNotNull(String key) {
        if (!aliasMapping.containsKey(key)) {
            createAlias(key);
        }
        return aliasMapping.get(key);
    }

    public List<EntityColumn> getWhereColumns() {
        return whereColumns;
    }

    public EntityTable setWhereColumns(List<EntityColumn> whereColumns) {
        this.whereColumns = whereColumns;
        return this;
    }

    public Set<EntityColumn> getSortColumns() {
        return sortColumns;
    }

    public EntityTable setSortColumns(Set<EntityColumn> sortColumns) {
        this.sortColumns = sortColumns;
        return this;
    }

    public void createAlias(String key) {
        if (!aliasMapping.containsKey(key)) {
            aliasMapping.put(key, generateAlias());
        }
    }

    private String generateAlias() {
        return (String.valueOf((char) (ALIAS_START + currentAliasCharIndex++)));
    }
}
