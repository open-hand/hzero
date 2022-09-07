package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_webhook_server.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-19-hmsg_webhook_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_webhook_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_webhook_server", remarks: "webhook配置") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "webhook编码")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 60 * weight + ")",  remarks: "webhook名称")  {constraints(nullable:"false")}  
            column(name: "server_type", type: "varchar(" + 30 * weight + ")",  remarks: "webhook类型，HMSG.WEBHOOK_TYPE")  {constraints(nullable:"false")}  
            column(name: "webhook_address", type: "varchar(" + 480 * weight + ")",  remarks: "webhook地址")  {constraints(nullable:"false")}  
            column(name: "secret", type: "varchar(" + 240 * weight + ")",  remarks: "秘钥")   
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "webhook描述")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_webhook_server",constraintName: "hmsg_webhook_server_u1")
    }
}