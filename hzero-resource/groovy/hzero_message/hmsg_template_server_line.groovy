package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_server_line.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_template_server_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_template_server_line_s', startValue:"1")
        }
        createTable(tableName: "hmsg_template_server_line", remarks: "消息模板账户关联明细") {
            column(name: "temp_server_line_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "temp_server_id", type: "bigint",  remarks: "消息模板账户,hmsg_template_server.temp_server_id")  {constraints(nullable:"false")}  
            column(name: "type_code", type: "varchar(" + 30 * weight + ")",  remarks: "模版类型，值集:HMSG.MESSAGE_TYPE")  {constraints(nullable:"false")}  
            column(name: "template_code", type: "varchar(" + 60 * weight + ")",  remarks: "消息模板编码，hmsg_message_template.template_code")  {constraints(nullable:"false")}  
            column(name: "server_id", type: "bigint",  remarks: "服务ID,hmsg_email_server或hmsg_sms_server")   
            column(name: "remark", type: "varchar(" + 480 * weight + ")",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"temp_server_id,type_code",tableName:"hmsg_template_server_line",constraintName: "hmsg_template_server_line_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-18-hmsg_template_server_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: "hmsg_template_server_line") {
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "服务编码")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-26-hmsg_template_server_line") {
        addColumn(tableName: 'hmsg_template_server_line') {
            column(name: "try_times", type: "int", remarks: "重试次数")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-04-27-hmsg_template_server_line") {
        addColumn(tableName: 'hmsg_template_server_line') {
            column(name: "enabled_flag", type: "tinyint", defaultValue:"1", remarks: "启用标识")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-15_hmsg_template_server_line") {
        dropUniqueConstraint(tableName: "hmsg_template_server_line", constraintName: "hmsg_template_server_line_u1")
        addUniqueConstraint(tableName: "hmsg_template_server_line", constraintName: "hmsg_template_server_line_u1", columnNames: "temp_server_id,type_code,template_code")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmsg_template_server_line") {
        addColumn(tableName: 'hmsg_template_server_line') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}