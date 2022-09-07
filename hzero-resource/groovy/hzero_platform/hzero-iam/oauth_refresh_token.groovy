package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_refresh_token.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_refresh_token") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_refresh_token_s', startValue:"1")
        }
        createTable(tableName: "oauth_refresh_token", remarks: "") {
            column(name: "token_id", type: "varchar(" + 128 * weight + ")",  remarks: "")  {constraints(primaryKey: true)} 
            column(name: "token", type: "blob",  remarks: "Token对象")   
            column(name: "authentication", type: "blob",  remarks: "授权对象")   

        }

    }

    changeSet(id: '2019-08-12-oauth_refresh_token', author: 'jiangzhou.bo@hand-china.com') {
        dropTable(tableName: 'oauth_refresh_token')
        if (helper.dbType().isSupportSequence()) {
            dropSequence(sequenceName: 'oauth_refresh_token_s')
        }
    }
}