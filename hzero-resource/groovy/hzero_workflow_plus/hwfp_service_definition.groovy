package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_service_definition.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero", id: "2019-06-06-hwfp_service_definition") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_service_definition_s', startValue:"1")
        }
        createTable(tableName: "hwfp_service_definition", remarks: "服务定义") {
            column(name: "service_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "category_id", type: "bigint",  remarks: "流程分类ID,hwfp_process_category.category_id")  {constraints(nullable:"false")}
            column(name: "document_id", type: "bigint",  remarks: "流程单据ID,hwfp_process_document.document_id")
            column(name: "service_mode", type: "varchar(" + 30 * weight + ")",  remarks: "服务方式，值集：HWFP.SERVICE_MODE")  {constraints(nullable:"false")}
            column(name: "service_type", type: "varchar(" + 30 * weight + ")",  remarks: "服务类别，值集：HWFP.SERVICE_TYPE")  {constraints(nullable:"false")}
            column(name: "service_code", type: "varchar(" + 30 * weight + ")",  remarks: "服务编码")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "服务描述")
            column(name: "interface_id", type: "bigint",  remarks: "远程调用必输，接口定义ID，hwfp_interface_definition.interface_id")
            column(name: "expression", type: "varchar(" + 120 * weight + ")",  remarks: "表达式必输")
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"service_code,tenant_id",tableName:"hwfp_service_definition",constraintName: "hwfp_service_definition_u1")
    }

    changeSet(author: "hzero", id: "2019-09-04-hwfp_service_definition") {
        addColumn(tableName: "hwfp_service_definition") {
            column(name: "simple_expression", type: "varchar(60)", remarks: "表达式必输，简易表达式")
        }
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-18-hwfp_service_definition") {
        modifyDataType(tableName: "hwfp_service_definition", columnName: 'expression', newDataType: "varchar(" + 360 * weight + ")")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-05-18-hwfp_service_definition-addColumn") {
        addColumn(tableName: "hwfp_service_definition") {
            column(name: "view_code", type: "varchar(" + 120 * weight + ")", remarks: "值集视图编码")
        }
        addColumn(tableName: "hwfp_service_definition") {
            column(name: "view_name", type: "varchar(" + 60 * weight + ")", remarks: "值集视图名称")
        }
    }

}