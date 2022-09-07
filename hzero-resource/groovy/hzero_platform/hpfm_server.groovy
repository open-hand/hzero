package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_server.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-02-hpfm_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_server_s', startValue:"1")
        }
        createTable(tableName: "hpfm_server", remarks: "文件服务器") {
            column(name: "SERVER_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "SERVER_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "服务器代码")  {constraints(nullable:"false")}  
            column(name: "SERVER_NAME", type: "varchar(" + 80 * weight + ")",  remarks: "服务器名称")  {constraints(nullable:"false")}  
            column(name: "SERVER_DESCRIPTION", type: "varchar(" + 240 * weight + ")",  remarks: "说明") 
            column(name: "PROTOCOL_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "协议FTP、SFTP(快码：HPFM.PROTOCOL_TYPE)")   {constraints(nullable:"false")}  
            column(name: "IP", type: "varchar(" + 30 * weight + ")",  remarks: "服务器IP")   {constraints(nullable:"false")}  
            column(name: "PORT", type: "bigint",  remarks: "服务器端口")  {constraints(nullable:"false")}  
            column(name: "LOGIN_USER", type: "varchar(" + 20 * weight + ")",  remarks: "登录用户")  {constraints(nullable:"false")}  
            column(name: "LOGIN_ENC_PWD", type: "varchar(" + 240 * weight + ")",  remarks: "加密密码")  {constraints(nullable:"false")}  
            column(name: "ENABLED_FLAG", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 1:启用 0：不启用")  {constraints(nullable:"false")}  
            column(name: "TENANT_ID", type: "bigint", defaultValue:"0",  remarks: "租户ID")   {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        addUniqueConstraint(columnNames: "SERVER_CODE,tenant_id", tableName: "hpfm_server", constraintName: "hpfm_server_u1")

    }
}