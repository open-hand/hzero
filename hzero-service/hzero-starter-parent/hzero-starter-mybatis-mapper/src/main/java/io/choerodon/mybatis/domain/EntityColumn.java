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

import io.choerodon.mybatis.util.StringUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.hzero.mybatis.common.query.JoinColumn;
import org.hzero.mybatis.common.query.JoinTable;
import org.hzero.mybatis.common.query.Where;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 数据库表对应的列
 *
 * @author liuzh
 */
public class EntityColumn {
    private EntityTable table;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private Class<? extends TypeHandler<?>> typeHandler;
    private EntityField field;
    private String sequenceName;
    private boolean id = false;
    private boolean uuid = false;
    private boolean identity = false;
    private String generator;
    //排序
    private String orderBy;
    //可查询
    private boolean selectable = true;
    //可插入
    private boolean insertable = true;
    //可更新
    private boolean updatable = true;

    private boolean multiLanguage = false;
    private boolean snowflakeEnable = true;

    //字段是否为 blob
    private boolean blob;

    private List<JoinTable> joinTables;
    private JoinColumn joinColumn;
    private Where where;
    private static Map<Class, String> typeMap;

    private Set<String> constraintNames = new HashSet<>();

    static {
        typeMap = new HashMap<>(16);
        typeMap.put(String.class, "VARCHAR");
        typeMap.put(BigDecimal.class, "DECIMAL");
        typeMap.put(Integer.class, "INTEGER");
        typeMap.put(Long.class, "BIGINT");
        typeMap.put(Double.class, "DOUBLE");
        typeMap.put(LocalDate.class, "DATE");
        typeMap.put(Date.class, "TIMESTAMP");
    }

    public EntityColumn() {
    }

    public EntityColumn(EntityTable table) {
        this.table = table;
    }

    public EntityField getField() {
        return field;
    }

    public void setField(EntityField field) {
        this.field = field;
    }

    public EntityTable getTable() {
        return table;
    }

    public void setTable(EntityTable table) {
        this.table = table;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(JdbcType jdbcType) {
        this.jdbcType = jdbcType;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(Class<? extends TypeHandler<?>> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public boolean isId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }

    public boolean isUuid() {
        return uuid;
    }

    public void setUuid(boolean uuid) {
        this.uuid = uuid;
    }

    public boolean isIdentity() {
        return identity;
    }

    public void setIdentity(boolean identity) {
        this.identity = identity;
    }

    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public boolean isBlob() {
        return blob;
    }

    public void setBlob(boolean blob) {
        this.blob = blob;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public EntityColumn setSelectable(boolean selectable) {
        this.selectable = selectable;
        return this;
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return String
     */
    public String getColumnEqualsHolder() {
        return getColumnEqualsHolder(null);
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名
     * @return String
     */
    public String getColumnEqualsHolder(String entityName) {
        return this.column + " = " + getColumnHolder(entityName);
    }

    /**
     * 返回多语言的列
     *
     * @param entityName 实体名
     * @return String
     */
    public String getColumnEqualsHolderTl(String entityName) {
        if (this.getTable().isMultiLanguage() && isMultiLanguage()) {
            return "t." + this.column + " = " + getColumnHolder(entityName);
        } else if (this.getTable().isMultiLanguage() && isId()) {
            return "b." + this.column + " = " + getColumnHolder(entityName);
        } else {
            return this.column + " = " + getColumnHolder(entityName);
        }

    }

    /**
     * 返回格式如:#{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return String
     */
    public String getColumnHolder() {
        return getColumnHolder(null);
    }

    /**
     * 返回格式如:#{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名
     * @return String
     */
    public String getColumnHolder(String entityName) {
        return getColumnHolder(entityName, null);
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName 实体名
     * @param suffix     后缀
     * @return String
     */
    public String getColumnHolder(String entityName, String suffix) {
        return getColumnHolder(entityName, suffix, null);
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName 实体名
     * @param suffix     后缀
     * @param separator  分隔符
     * @return String
     */
    public String getColumnHolder(String entityName, String suffix, String separator) {
        StringBuilder sb = new StringBuilder("#{");
        if (StringUtil.isNotEmpty(entityName)) {
            sb.append(entityName);
            sb.append(".");
        }
        sb.append(this.property);
        if (StringUtil.isNotEmpty(suffix)) {
            sb.append(suffix);
        }
        if (this.jdbcType != null) {
            sb.append(",jdbcType=");
            sb.append(this.jdbcType.toString());
        }
        if (this.jdbcType == null && typeMap.containsKey(this.javaType)) {
            sb.append(",jdbcType=");
            sb.append(typeMap.get(this.javaType));
        }
        if (this.typeHandler != null) {
            sb.append(",typeHandler=");
            sb.append(this.typeHandler.getCanonicalName());
        }
        if (!this.javaType.isArray()) {
            //当类型为数组时，不设置javaType#103
            sb.append(",javaType=");
            sb.append(javaType.getCanonicalName());
        }
        sb.append("}");
        if (StringUtil.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler},
     *
     * @param entityName 实体名
     * @param suffix     后缀
     * @return String
     */
    public String getColumnHolderWithComma(String entityName, String suffix) {
        return getColumnHolder(entityName, suffix, ",");
    }

    public boolean isMultiLanguage() {
        return multiLanguage;
    }

    public void setMultiLanguage(boolean multiLanguage) {
        this.multiLanguage = multiLanguage;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    public EntityColumn setJoinTables(List<JoinTable> joinTables) {
        this.joinTables = joinTables;
        return this;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public EntityColumn setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public Where getWhere() {
        return where;
    }

    public EntityColumn setWhere(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityColumn that = (EntityColumn) o;
        if (id != that.id || uuid != that.uuid || identity != that.identity || jdbcType != that.jdbcType) {
            return false;
        }

        if (table != null ? !table.equals(that.table) : that.table != null) {
            return false;
        }
        if (property != null ? !property.equals(that.property) : that.property != null) {
            return false;
        }
        if (column != null ? !column.equals(that.column) : that.column != null) {
            return false;
        }
        if (javaType != null ? !javaType.equals(that.javaType) : that.javaType != null) {
            return false;
        }
        if (typeHandler != null ? !typeHandler.equals(that.typeHandler) : that.typeHandler != null) {
            return false;
        }
        if (sequenceName != null ? !sequenceName.equals(that.sequenceName) : that.sequenceName != null) {
            return false;
        }
        if (generator != null ? !generator.equals(that.generator) : that.generator != null) {
            return false;
        }
        return !(orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null);
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (property != null ? property.hashCode() : 0);
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        result = 31 * result + (jdbcType != null ? jdbcType.hashCode() : 0);
        result = 31 * result + (typeHandler != null ? typeHandler.hashCode() : 0);
        result = 31 * result + (sequenceName != null ? sequenceName.hashCode() : 0);
        result = 31 * result + (id ? 1 : 0);
        result = 31 * result + (uuid ? 1 : 0);
        result = 31 * result + (identity ? 1 : 0);
        result = 31 * result + (generator != null ? generator.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        return result;
    }

    public JoinTable findJoinTableByName(String joinName) {
        JoinTable joinTable = null;
        if (this.joinTables != null && joinName != null) {
            for (JoinTable jt : this.joinTables) {
                if (joinName.equalsIgnoreCase(jt.name())) {
                    joinTable = jt;
                    break;
                }
            }
        }
        return joinTable;
    }

    public void addJoinTable(JoinTable joinTable) {
        if (this.joinTables == null) {
            this.joinTables = new ArrayList<>();
        }
        this.joinTables.add(joinTable);
    }

    public Set<String> getConstraintNames() {
        return constraintNames;
    }

    public EntityColumn setConstraintNames(Set<String> constraintNames) {
        this.constraintNames = constraintNames;
        return this;
    }

    public boolean isSnowflakeEnable() {
        return snowflakeEnable;
    }

    public EntityColumn setSnowflakeEnable(boolean snowflakeEnable) {
        this.snowflakeEnable = snowflakeEnable;
        return this;
    }
}
