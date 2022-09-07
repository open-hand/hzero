package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_data_config.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-08-17-hmnt_audit_data_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_data_config_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_data_config", remarks: "数据审计配置") {
            column(name: "audit_data_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "entity_table_id", type: "bigint",  remarks: "实体表ID，HPFM_ENTITY_TABLE.ENTITY_TABLE_ID")   
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名，冗余")  {constraints(nullable:"false")}  
            column(name: "table_name", type: "varchar(" + 30 * weight + ")",  remarks: "表名称，冗余")  {constraints(nullable:"false")}  
            column(name: "display_name", type: "longtext",  remarks: "展示名称")   
            column(name: "audit_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否审计")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"entity_table_id,tenant_id",tableName:"hmnt_audit_data_config",constraintName: "hmnt_audit_data_config_u1")
    }
}