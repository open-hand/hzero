package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_auth.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_interface_auth") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_auth_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_auth", remarks: "接口认证管理") {
            column(name: "interface_auth_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "interface_id", type: "bigint",  remarks: "接口ID")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "auth_level", type: "varchar(" + 30 * weight + ")",   defaultValue:"SERVER",   remarks: "认证层级，代码HITF.AUTH_LEVEL")  {constraints(nullable:"false")}
            column(name: "auth_level_value", type: "varchar(" + 128 * weight + ")",  remarks: "认证层级值")
            column(name: "auth_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"NONE",   remarks: "认证模式，代码：HITF.AUTH_TYPE")  {constraints(nullable:"false")}
            column(name: "grant_type", type: "varchar(" + 30 * weight + ")",  remarks: "授权模式，代码：HITF.GRANT_TYPE")
            column(name: "access_token_url", type: "varchar(" + 255 * weight + ")",  remarks: "获取Token的URL")
            column(name: "client_id", type: "varchar(" + 255 * weight + ")",  remarks: "客户端ID")
            column(name: "client_secret", type: "varchar(" + 255 * weight + ")",  remarks: "客户端密钥")
            column(name: "auth_username", type: "varchar(" + 80 * weight + ")",  remarks: "认证用户名")
            column(name: "auth_password", type: "varchar(" + 255 * weight + ")",  remarks: "认证密码")
            column(name: "soap_wss_password_type", type: "varchar(" + 30 * weight + ")",  remarks: "SOAP加密类型")
            column(name: "soap_username", type: "varchar(" + 80 * weight + ")",  remarks: "校验用户名")
            column(name: "soap_password", type: "varchar(" + 255 * weight + ")",  remarks: "校验密码")
            column(name: "remark", type: "longtext",  remarks: "备注说明")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"interface_id,auth_level,auth_level_value",tableName:"hitf_interface_auth",constraintName: "hitf_interface_auth_u1")
    }

    changeSet(author: "jianbo.li@hand-china.com", id: "2019-07-09-hitf_interface_auth") {
        dropNotNullConstraint(columnName:"interface_id",columnDataType:"bigint",tableName: "hitf_interface_auth")
    }

    changeSet(author: "jianbo.li@hand-china.com",id: "2019-08-15-hitf_interface_auth"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName:"hitf_interface_auth"){
            column(name: "password_encode_type", type: "varchar(" + 30 * weight + ")",remarks: "密码加密类型")
        }
    }

    changeSet(author: "mingke.yan@hand-china.com",id: "2020-06-04-hitf_interface_auth"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName:"hitf_interface_auth"){
            column(name: "mime_type", type: "varchar(" + 120 * weight + ")",remarks: "请求内容类型")
        }
        addColumn(tableName:"hitf_interface_auth"){
            column(name: "content_charset", type: "varchar(" + 60 * weight + ")",remarks: "字符集")
        }
    }
}
