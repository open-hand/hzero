package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_document_opr.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-10-hmnt_audit_document_opr") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_document_opr_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_document_opr", remarks: "单据审计关联操作审计") {
            column(name: "audit_document_opr_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "audit_document_id", type: "bigint",  remarks: "单据审计配置ID,hmnt_audit_document.audit_document_id")  {constraints(nullable:"false")}  
            column(name: "audit_op_config_id", type: "bigint",  remarks: "操作审计配置ID,hmnt_audit_op_config.audit_op_config_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"audit_document_id,audit_op_config_id",tableName:"hmnt_audit_document_opr",constraintName: "hmnt_audit_document_opr_u1")
    }
}