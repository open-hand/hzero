package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_client.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_client") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_client_s', startValue:"1")
        }
        createTable(tableName: "oauth_client", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "name", type: "varchar(" + 32 * weight + ")",  remarks: "客户端名称")  {constraints(nullable:"false")}
            column(name: "organization_id", type: "bigint",  remarks: "组织ID")  {constraints(nullable:"false")}
            column(name: "resource_ids", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "资源ID列表，目前只使用default")
            column(name: "secret", type: "varchar(" + 255 * weight + ")",  remarks: "客户端秘钥")
            column(name: "scope", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "Oauth授权范围")
            column(name: "authorized_grant_types", type: "varchar(" + 255 * weight + ")",  remarks: "支持的授权类型列表")
            column(name: "web_server_redirect_uri", type: "varchar(" + 128 * weight + ")",  remarks: "授权重定向URL")
            column(name: "access_token_validity", type: "bigint",  remarks: "客户端特定的AccessToken超时时间")
            column(name: "refresh_token_validity", type: "bigint",  remarks: "客户端特定的RefreshToken超时时间")
            column(name: "additional_information", type: "varchar(" + 1024 * weight + ")",  remarks: "客户端附加信息")
            column(name: "auto_approve", type: "varchar(" + 32 * weight + ")",   defaultValue:"default",   remarks: "自动授权范围列表")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")

        }

        addUniqueConstraint(columnNames:"name",tableName:"oauth_client",constraintName: "oauth_client_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-08-06-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "enabled_flag", type: "tinyint", defaultValue:"1", remarks: "启用标识")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-04-26-oauth_client") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'oauth_client') {
            column(name: "access_roles", type: "varchar(" + 240 * weight + ")", remarks: "客户端可访问角色")
        }
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-04-28-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "pwd_replay_flag", type: "tinyint", defaultValue:"0", remarks: "密码防重放标识") {constraints(nullable:"false")}
        }
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-05-09-oauth_client") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'oauth_client') {
            column(name: "time_zone", type: "varchar(" + 16 * weight + ")", defaultValue:"GMT+8", remarks: "时区") {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-19-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "api_encrypt_flag", type: "tinyint", defaultValue:"1", remarks: "接口加密标识") {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-21-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "api_replay_flag", type: "tinyint", defaultValue:"0", remarks: "API防重放标识") {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-11-oauth_client") {
        addColumn(tableName: 'oauth_client') {
            column(name: "password_encrypt_flag", type: "tinyint", defaultValue:"1", remarks: "密码是否加密传输") {constraints(nullable:"false")}
        }
    }
}