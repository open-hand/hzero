package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_client_auth.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_client_auth") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_client_auth_s', startValue:"1")
        }
        createTable(tableName: "hitf_client_auth", remarks: "客户端授权表") {
            column(name: "client_oauth_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "client_id", type: "varchar(" + 128 * weight + ")",  remarks: "客户端ID")  {constraints(nullable:"false")}  
            column(name: "statistics_level", type: "varchar(" + 30 * weight + ")",  remarks: "统计维度，代码HITF.STATISTICS_LEVEL")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"client_id",tableName:"hitf_client_auth",constraintName: "hitf_client_auth_u1")
    }
}