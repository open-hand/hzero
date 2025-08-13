package script.db

databaseChangeLog(logicalFilePath: 'script/db/fd_language.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-fd_language") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'fd_language_s', startValue:"1")
        }
        createTable(tableName: "fd_language", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "code", type: "varchar(" + 32 * weight + ")",  remarks: "语言Code")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 32 * weight + ")",  remarks: "语言名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 128 * weight + ")",  remarks: "描述")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"code",tableName:"fd_language",constraintName: "fd_language_u1")
        addUniqueConstraint(columnNames:"name",tableName:"fd_language",constraintName: "fd_language_u2")
    }
}