package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_email_server.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_email_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_email_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_email_server", remarks: "邮箱服务") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "服务代码")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 240 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "host", type: "varchar(" + 60 * weight + ")",  remarks: "服务器")  {constraints(nullable:"false")}  
            column(name: "port", type: "varchar(" + 10 * weight + ")",  remarks: "端口")  {constraints(nullable:"false")}  
            column(name: "try_times", type: "int",  remarks: "重试次数")   
            column(name: "username", type: "varchar(" + 30 * weight + ")",  remarks: "用户名")   
            column(name: "password_encrypted", type: "varchar(" + 120 * weight + ")",  remarks: "密码")
            column(name: "sender", type: "varchar(" + 60 * weight + ")",  remarks: "发件人")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_email_server",constraintName: "hmsg_email_server_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-05-10-hmsg_email_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_email_server') {
            if(helper.isOracle()){
                column(name: "protocol", type: "varchar(" + 30 * weight + ")", defaultValue: "",  remarks: "协议，值集：HMSG.EMAIL_PROTOCOL")  {constraints(nullable:"false")}
            } else {
                column(name: "protocol", type: "varchar(" + 30 * weight + ")", remarks: "协议，值集：HMSG.EMAIL_PROTOCOL")  {constraints(nullable:"false")}
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-06-04-hmsg_email_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hmsg_email_server') {
            column(name: "filter_strategy", type: "varchar(" + 30 * weight + ")", remarks: "筛选策略，值集：HMSG.EMAIL.FILTER_STRATEGY")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-16-hmsg_email_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hmsg_email_server", columnName: 'username', newDataType: "varchar(" + 60 * weight + ")")
    }
}