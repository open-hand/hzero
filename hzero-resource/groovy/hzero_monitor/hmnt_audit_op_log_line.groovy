package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_op_log_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hmnt_audit_op_log_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmnt_audit_op_log_line_s', startValue:"1")
        }
        createTable(tableName: "hmnt_audit_op_log_line", remarks: "操作审计记录行") {
            column(name: "log_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "log_id", type: "bigint",  remarks: "操作审计记录ID，hmnt_audit_op_line.audit_op_id")  {constraints(nullable:"false")}  
            column(name: "log_type", type: "varchar(" + 30 * weight + ")",  remarks: "记录类型，值集HMNT.AUDIT_LOG_TYPE[PARAMETER(参数),RESULT(结果)]")  {constraints(nullable:"false")}  
            column(name: "log_content", type: "longtext",  remarks: "参数内容")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmnt_audit_op_log_line") {
        addColumn(tableName: 'hmnt_audit_op_log_line') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}