package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_sms_server.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_sms_server") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_sms_server_s', startValue:"1")
        }
        createTable(tableName: "hmsg_sms_server", remarks: "短信服务") {
            column(name: "server_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "服务代码")  {constraints(nullable:"false")}
            column(name: "server_name", type: "varchar(" + 240 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}
            column(name: "server_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "短信服务类型，值集:HMSG.SMS_SERVER_TYPE")  {constraints(nullable:"false")}
            column(name: "end_point", type: "varchar(" + 120 * weight + ")",  remarks: "EndPoint")
            column(name: "access_key", type: "varchar(" + 120 * weight + ")",  remarks: "AccessKeyId")  {constraints(nullable:"false")}
            column(name: "access_key_secret", type: "varchar(" + 120 * weight + ")",  remarks: "AccessKeySecret")  {constraints(nullable:"false")}
            column(name: "sign_name", type: "varchar(" + 30 * weight + ")",  remarks: "短信签名")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"server_code,tenant_id",tableName:"hmsg_sms_server",constraintName: "hmsg_sms_server_u1")
    }
}