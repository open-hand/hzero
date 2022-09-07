package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_mail_approved_history.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-07-hwfp_mail_approved_history") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_mail_approved_history_s', startValue:"1")
        }
        createTable(tableName: "hwfp_mail_approved_history", remarks: "邮件审批历史") {
            column(name: "history_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "protocol", type: "varchar(" + 30 * weight + ")",  remarks: "协议")  {constraints(nullable:"false")}  
            column(name: "approved_subject", type: "varchar(" + 30 * weight + ")",  remarks: "邮件主题")   
            column(name: "approved_body", type: "longtext",  remarks: "邮件内容")   
            column(name: "from_mail_account", type: "varchar(" + 30 * weight + ")",  remarks: "来源邮箱")  {constraints(nullable:"false")}  
            column(name: "mail_type", type: "varchar(" + 30 * weight + ")",  remarks: "send发送/receive接收")  {constraints(nullable:"false")}  
            column(name: "dispose_status", type: "varchar(" + 30 * weight + ")",  remarks: "处理状态SUCCESS成功/ERROR失败")  {constraints(nullable:"false")}  
            column(name: "task_id", type: "bigint",  remarks: "任务ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "process_instance_id", type: "bigint",  remarks: "流程实例ID")  {constraints(nullable:"false")}  
            column(name: "mail_account_code", type: "varchar(" + 30 * weight + ")",  remarks: "邮箱账号编码")  {constraints(nullable:"false")}  
            column(name: "error_message", type: "longtext",  remarks: "错误信息")   
            column(name: "mail_process_count", type: "bigint",  remarks: "邮件处理总数")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}