package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_auth_dim.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_doc_type_auth_dim") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_auth_dim_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type_auth_dim", remarks: "单据类型权限维度定义") {
            column(name: "auth_dim_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "doc_type_id", type: "bigint",  remarks: "单据类型ID")  {constraints(nullable:"false")}  
            column(name: "auth_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型代码，HIAM.AUTHORITY_TYPE_CODE")  {constraints(nullable:"false")}  
            column(name: "source_match_table", type: "varchar(" + 30 * weight + ")",  remarks: "来源匹配表名")   
            column(name: "source_match_field", type: "varchar(" + 30 * weight + ")",  remarks: "采购方匹配字段，匹配Mapper中的相关字段")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "sup_source_match_field", type: "varchar(" + 30 * weight + ")",  remarks: "供应方匹配字段，匹配Mapper中的相关字段")   

        }

        addUniqueConstraint(columnNames:"doc_type_id,auth_type_code",tableName:"hiam_doc_type_auth_dim",constraintName: "hiam_doc_type_auth_dim_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hiam_doc_type_auth_dim") {
        dropColumn(tableName: 'hiam_doc_type_auth_dim', columnName: 'sup_source_match_field')
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-03-hiam_doc_type_auth_dim"){
        addColumn(tableName: 'hiam_doc_type_auth_dim') {
            column(name: "rule_type", type: "varchar(30)", defaultValue: "COLUMN", remarks: "规则类型，值集HIAM.DOC.RULE_TYPE[COLUMN,SUB_SELECT]") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-04-hiam_doc_type_auth_dim"){
        modifyDataType(tableName: "hiam_doc_type_auth_dim", columnName: 'source_match_field', newDataType: "varchar(1200)")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-28-hiam_doc_type_auth_dim"){
        update(tableName:'hiam_doc_type_auth_dim'){
            column(name:'auth_type_code', value:'INV_ORGANIZATION')
            where "auth_type_code='INVORG'"
        }
        update(tableName:'hiam_doc_type_auth_dim'){
            column(name:'auth_type_code', value:'PURCHASE_ORGANIZATION')
            where "auth_type_code='PURORG'"
        }
        update(tableName:'hiam_doc_type_auth_dim'){
            column(name:'auth_type_code', value:'PURCHASE_AGENT')
            where "auth_type_code='PURAGENT'"
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hiam_doc_type_auth_dim") {
        addColumn(tableName: 'hiam_doc_type_auth_dim') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}
