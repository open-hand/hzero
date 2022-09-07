package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_service_parameter.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero", id: "2019-06-06-hwfp_service_parameter") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_service_parameter_s', startValue:"1")
        }
        createTable(tableName: "hwfp_service_parameter", remarks: "服务定义参数") {
            column(name: "parameter_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "service_id", type: "bigint",  remarks: "服务定义ID，hwfl_service_definition.service_id")  {constraints(nullable:"false")}
            column(name: "interface_parameter_id", type: "bigint",  remarks: "接口定义参数ID，hwfl_interface_parameter.parameter_id")
            column(name: "parameter_name", type: "varchar(" + 30 * weight + ")",  remarks: "接口参数名称")  {constraints(nullable:"false")}
            column(name: "parameter_source", type: "varchar(" + 30 * weight + ")",  remarks: "参数来源类型，值集：HWFP.SERVICE.PARAMETER_SOURCE")  {constraints(nullable:"false")}
            column(name: "parameter_value", type: "varchar(" + 120 * weight + ")",  remarks: "参数值")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"service_id,parameter_name",tableName:"hwfp_service_parameter",constraintName: "hwfp_service_parameter_u1")
    }

    changeSet(author: "hzero", id: "2019-09-04-hwfp_service_parameter01") {
        addColumn(tableName: "hwfp_service_parameter") {
            column(name: "operator", type: "varchar(30)", remarks: "操作符")
        }
    }

    changeSet(author: "hzero", id: "2019-09-04-hwfp_service_parameter02") {
        addColumn(tableName: "hwfp_service_parameter") {
            column(name: "right_parameter_source", type: "varchar(30)", remarks: "右参数来源")
        }
    }

    changeSet(author: "hzero", id: "2019-09-04-hwfp_service_parameter03") {
        addColumn(tableName: "hwfp_service_parameter") {
            column(name: "right_parameter_value", type: "varchar(" + weight * 120  + ")", remarks: "右参数值")
        }
    }
}