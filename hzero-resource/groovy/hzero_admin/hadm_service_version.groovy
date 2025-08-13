package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_service_version.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_service_version") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_service_version_s', startValue:"1")
        }
        createTable(tableName: "hadm_service_version", remarks: "服务版本") {
            column(name: "service_version_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "service_id", type: "bigint",  remarks: "服务ID：hadm_service.service_id")  {constraints(nullable:"false")}
            column(name: "version_number", type: "varchar(" + 60 * weight + ")",  remarks: "版本号")  {constraints(nullable:"false")}  
            column(name: "meta_version", type: "varchar(" + 30 * weight + ")",  remarks: "标记版本")  {constraints(nullable:"false")}  
            column(name: "release_date", type: "datetime",  remarks: "发布时间")  {constraints(nullable:"false")}  
            column(name: "update_log", type: "longtext",  remarks: "更新日志")   
            column(name: "service_version_config", type: "varchar(" + 1000 * weight + ")",  remarks: "版本配置")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"service_id,version_number",tableName:"hadm_service_version",constraintName: "hadm_service_version_u1")
    }
}