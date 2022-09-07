package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_document.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-10-hmnt_audit_document") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_document_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_document", remarks: "单据审计配置") {
            column(name: "audit_document_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "document_code", type: "varchar(" + 60 * weight + ")",  remarks: "单据编码")  {constraints(nullable:"false")}  
            column(name: "document_name", type: "varchar(" + 120 * weight + ")",  remarks: "单据名称")  {constraints(nullable:"false")}  
            column(name: "document_description", type: "varchar(" + 480 * weight + ")",  remarks: "单据描述")   
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"document_code,tenant_id",tableName:"hmnt_audit_document",constraintName: "hmnt_audit_document_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-09-21-hmnt_audit_document-01') {
        addColumn(tableName: 'hmnt_audit_document') {
            column(name: 'page_route', type: "varchar(1000)", remarks: '业务路由')
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-09-21-hmnt_audit_document-02') {
        addColumn(tableName: 'hmnt_audit_document') {
            column(name: 'business_key', type: "varchar(480)", remarks: '路由中的业务主键名称')
        }
    }
}