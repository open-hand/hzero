package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_access_token.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_access_token") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_access_token_s', startValue:"1")
        }
        createTable(tableName: "oauth_access_token", remarks: "") {
            column(name: "token_id", type: "varchar(" + 128 * weight + ")",  remarks: "")  {constraints(primaryKey: true)} 
            column(name: "token", type: "blob",  remarks: "Token对象")   
            column(name: "authentication_id", type: "varchar(" + 255 * weight + ")",  remarks: "授权ID，用于索引授权对象")   
            column(name: "user_name", type: "varchar(" + 32 * weight + ")",  remarks: "用户名")   
            column(name: "client_id", type: "varchar(" + 32 * weight + ")",  remarks: "Client ID")   
            column(name: "authentication", type: "blob",  remarks: "授权对象")   
            column(name: "refresh_token", type: "varchar(" + 128 * weight + ")",  remarks: "Refresh Token ID")   

        }

    }
}