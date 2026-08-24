/**
 * Copyright 2016 www.extdo.com 
 */
package org.hzero.mybatis.common.query;


import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author njq.niu@hand-china.com
 */
public class SQLField {

    private String field;
    
    public SQLField(String field){
        setField(field);
    }
    

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }


    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof SQLField))
            return false;

        SQLField sqlField = (SQLField) obj;
        return StringUtils.equals(field, sqlField.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(field);
    }
}
