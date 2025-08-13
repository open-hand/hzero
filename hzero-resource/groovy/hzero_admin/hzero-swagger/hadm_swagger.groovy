package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_swagger.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hadm_swagger") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_swagger_s', startValue:"1")
        }
        createTable(tableName: "hadm_swagger", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "service_name", type: "varchar(" + 128 * weight + ")",  remarks: "服务名,如hap-user-service")  {constraints(nullable:"false")}  
            column(name: "service_version", type: "varchar(" + 64 * weight + ")",   defaultValue:"0.0.0",   remarks: "服务版本")  {constraints(nullable:"false")}  
            column(name: "value", type: "mediumtext",  remarks: "接口文档json数据")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"service_name,service_version",tableName:"hadm_swagger",constraintName: "hadm_swagger_u1")

    }
}