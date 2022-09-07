package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_dingtalk_server.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hmsg_dingtalk_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_dingtalk_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_dingtalk_server", remarks: "钉钉配置") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}  
            column(name: "server_name", type: "varchar(" + 60 * weight + ")",  remarks: "配置名称")  {constraints(nullable:"false")}  
            column(name: "auth_type", type: "varchar(" + 30 * weight + ")",  remarks: "授权类型，值集：HMSG.DINGTALK.AUTH_TYPE")  {constraints(nullable:"false")}  
            column(name: "app_key", type: "varchar(" + 60 * weight + ")",  remarks: "应用key")   
            column(name: "app_secret", type: "varchar(" + 240 * weight + ")",  remarks: "应用密钥")   
            column(name: "auth_address", type: "varchar(" + 480 * weight + ")",  remarks: "第三方授权地址")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_dingtalk_server",constraintName: "hmsg_dingtalk_server_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-14-hmsg_dingtalk_server") {
        addColumn(tableName: "hmsg_dingtalk_server") {
            column(name: "agent_id", type: "bigint",  remarks: "默认应用ID")
        }
    }
}