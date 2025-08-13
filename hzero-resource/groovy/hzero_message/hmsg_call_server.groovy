package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_call_server.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-26-hmsg_call_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_call_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_call_server", remarks: "语音消息服务") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 60 * weight + ")",  remarks: "配置名称")  {constraints(nullable:"false")}  
            column(name: "server_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "服务类型，值集：HMSG.CALL.SERVER_TYPE")  {constraints(nullable:"false")}  
            column(name: "access_key", type: "varchar(" + 240 * weight + ")",  remarks: "aapId或accessKeyId")  {constraints(nullable:"false")}  
            column(name: "access_secret", type: "varchar(" + 480 * weight + ")",  remarks: "密钥")
            column(name: "ext_param", type: "varchar(" + 480 * weight + ")",  remarks: "配置扩展参数")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_call_server",constraintName: "hmsg_call_server_u1")
    }
}