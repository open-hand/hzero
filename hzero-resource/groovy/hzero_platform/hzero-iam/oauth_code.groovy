package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_code.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-oauth_code") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'oauth_code_s', startValue:"1")
        }
        createTable(tableName: "oauth_code", remarks: "") {
            column(name: "code", type: "varchar(" + 32 * weight + ")",  remarks: "")  {constraints(primaryKey: true)} 
            column(name: "authentication", type: "blob",  remarks: "授权对象")   

        }

    }
}