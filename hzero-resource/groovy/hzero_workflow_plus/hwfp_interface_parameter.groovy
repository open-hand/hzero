package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_interface_parameter.groovy') {
    changeSet(author: "hzero", id: "2019-06-06-hwfp_interface_parameter") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_interface_parameter_s', startValue:"1")
        }
        createTable(tableName: "hwfp_interface_parameter", remarks: "接口定义参数") {
            column(name: "parameter_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "interface_id", type: "bigint",  remarks: "接口定义ID，hwfp_interface_definition.interface_id")  {constraints(nullable:"false")}  
            column(name: "parameter_name", type: "varchar(" + 30 * weight + ")",  remarks: "接口参数名称")  {constraints(nullable:"false")}  
            column(name: "parameter_type", type: "varchar(" + 30 * weight + ")",  remarks: "接口参数类型，[值集]HWFP.INTERFACE.PARAM_TYPE")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "参数描述")   
            column(name: "default_value", type: "varchar(" + 240 * weight + ")",  remarks: "默认值")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"interface_id,parameter_name",tableName:"hwfp_interface_parameter",constraintName: "hwfp_interface_parameter_u1")
    }
}