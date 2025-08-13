package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_hystrix_conf_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_hystrix_conf_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_hystrix_conf_line_s', startValue:"1")
        }
        createTable(tableName: "hadm_hystrix_conf_line", remarks: "") {
            column(name: "conf_line_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "conf_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "property_name", type: "varchar(" + 240 * weight + ")",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "property_value", type: "varchar(" + 120 * weight + ")",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "")   
            column(name: "remark", type: "longtext",  remarks: "")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"conf_id,property_name",tableName:"hadm_hystrix_conf_line",constraintName: "hadm_hystrix_conf_line_U1")
    }
}