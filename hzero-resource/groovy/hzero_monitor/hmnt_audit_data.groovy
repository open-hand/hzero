package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_data.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-08-17-hmnt_audit_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_data_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_data", remarks: "数据审计") {
            column(name: "audit_data_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "audit_data_config_id", type: "bigint",  remarks: "数据审计配置ID")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名，冗余")  {constraints(nullable:"false")}  
            column(name: "table_name", type: "varchar(" + 30 * weight + ")",  remarks: "表名称，冗余")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "audit_type", type: "varchar(" + 30 * weight + ")",  remarks: "审计类型，增删改")  {constraints(nullable:"false")}  
            column(name: "entity_code", type: "varchar(" + 128 * weight + ")",  remarks: "审计实体代码")  {constraints(nullable:"false")}  
            column(name: "entity_id", type: "bigint",  remarks: "审计实体ID")  {constraints(nullable:"false")}  
            column(name: "entity_version", type: "bigint",  remarks: "审计实体版本")   
            column(name: "process_user_id", type: "bigint",  remarks: "操作用户ID")  {constraints(nullable:"false")}  
            column(name: "process_time", type: "datetime",  remarks: "操作时间")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"service_name,entity_code,entity_id,entity_version,tenant_id",tableName:"hmnt_audit_data",constraintName: "hmnt_audit_data_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-01-04-hmnt_audit_data') {
        addColumn(tableName: 'hmnt_audit_data') {
            column(name: 'audit_batch_number', type: "varchar(60)", remarks: '审计批次号，hmnt_audit_op_log.audit_batch_number')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-07-10-hmnt_audit_data') {
        addColumn(tableName: 'hmnt_audit_data') {
            column(name: 'menu_id', type: "bigint", remarks: '菜单ID')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-08-25-hmnt_audit_data-1') {
        dropUniqueConstraint(tableName: 'hmnt_audit_data', constraintName: "hmnt_audit_data_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-08-25-hmnt_audit_data-2') {
        createIndex(tableName: "hmnt_audit_data", indexName: "hmnt_audit_data_n1") {
            column(name: "service_name")
            column(name: "entity_code")
            column(name: "entity_id")
            column(name: "entity_version")
            column(name: "tenant_id")
        }
    }
}