package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_email_property.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_email_property") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_email_property_s', startValue:"1")
        }
        createTable(tableName: "hmsg_email_property", remarks: "邮箱属性") {
            column(name: "property_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_id", type: "bigint",  remarks: "邮箱服务ID，hmsg_email_server.server_id")  {constraints(nullable:"false")}  
            column(name: "property_code", type: "varchar(" + 30 * weight + ")",  remarks: "属性代码")  {constraints(nullable:"false")}  
            column(name: "property_value", type: "varchar(" + 60 * weight + ")",  remarks: "属性值")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_id,tenant_id,property_code",tableName:"hmsg_email_property",constraintName: "hmsg_email_property_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-08-hmsg_email_property") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropUniqueConstraint(tableName: 'hmsg_email_property', constraintName: 'hmsg_email_property_u1')
        modifyDataType(tableName: "hmsg_email_property", columnName: 'property_code', newDataType: "varchar(" + 240 * weight + ")")
        addUniqueConstraint(tableName: 'hmsg_email_property', columnNames: 'server_id,tenant_id,property_code', constraintName: 'hmsg_email_property_u1')
    }
}