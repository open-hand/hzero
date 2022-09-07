package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_password_history.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_password_history") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_password_history_s', startValue:"1")
        }
        createTable(tableName: "oauth_password_history", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户id")  {constraints(nullable:"false")}  
            column(name: "password", type: "varchar(" + 128 * weight + ")",  remarks: "Hash之后的密码")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

    }

    changeSet(author: 'hzero@hand-china.com', id: '2019-04-25-rename-password-oauth-password-history') {
        renameColumn(columnDataType: 'VARCHAR(128)', newColumnName: "HASH_PASSWORD", oldColumnName: "password", remarks: 'Hash后的用户密码', tableName: 'OAUTH_PASSWORD_HISTORY')
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-oauth_password_history") {
        addColumn(tableName: 'oauth_password_history') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}