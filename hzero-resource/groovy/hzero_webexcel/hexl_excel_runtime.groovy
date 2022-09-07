// demo.groovy
package script.db

databaseChangeLog(logicalFilePath: 'hexl_excel_runtime.groovy') {
    changeSet(id: '2019-09-19-webexcel-hexl_excel_runtime', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hexl_excel_runtime_s', startValue: "1")
        }
        createTable(tableName: "hexl_excel_runtime") {
            column(name: "runtime_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "template_code", type: "varchar(" + 255 * weight + ")", remarks: "模板代码") {
                constraints(nullable: "false")
            }
            column(name: "template_version", type: "bigint", remarks: "模板版本号") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "varchar(" + 255 * weight + ")", remarks: "说明")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用,1启用，0未启用")
            column(name: "sheets", type: "longtext", remarks: "excel对应的json格式数据")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户id") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
    }

    changeSet(id: '2019-10-17-webexcel-hexl_excel_runtime', author: 'hzero@hand-china.com') {
        addUniqueConstraint(columnNames:"description",tableName:"hexl_excel_runtime",constraintName: "hexl_excel_runtime_u1")
    }

    changeSet(id: '2020-06-16-webexcel-hexl_excel_runtime', author: 'fanghan.liu@hand-china.com') {
        dropUniqueConstraint(tableName:"hexl_excel_runtime",constraintName: "hexl_excel_runtime_u1")
        addUniqueConstraint(columnNames:"description, tenant_id",tableName:"hexl_excel_runtime",constraintName: "hexl_excel_runtime_u1")
    }
}