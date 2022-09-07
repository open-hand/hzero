package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_interface_definition.groovy') {
    changeSet(author: "hzero", id: "2019-06-06-hwfp_interface_definition") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_interface_definition_s', startValue:"1")
        }
        createTable(tableName: "hwfp_interface_definition", remarks: "接口定义") {
            column(name: "interface_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "interface_code", type: "varchar(" + 60 * weight + ")",  remarks: "接口编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "接口描述")   
            column(name: "service_name", type: "varchar(" + 90 * weight + ")",  remarks: "服务名称")  {constraints(nullable:"false")}  
            column(name: "permission_code", type: "varchar(" + 128 * weight + ")",  remarks: "接口权限表示，iam_permission.code")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"interface_code,tenant_id",tableName:"hwfp_interface_definition",constraintName: "hwfp_interface_definition_u1")
    }
}