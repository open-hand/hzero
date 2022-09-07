// demo.groovy
package script.db

databaseChangeLog(logicalFilePath: 'hexl_excel_template.groovy') {
    changeSet(id: '2019-09-19-webexcel-hexl_excel_template', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hexl_excel_template_s', startValue: "1")
        }

        createTable(tableName: "hexl_excel_template") {
            column(name: "template_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "template_code", type: "varchar("+ 255 * weight +")", remarks: "模板代码") {
                constraints(nullable: "false")
            }
            column(name: "template_version", type: "bigint", remarks: "模板版本号") {
                constraints(nullable: "false")
            }
            column(name: "sheets", type: "longtext", remarks: "excel对应的json格式数据")
            column(name: "description", type: "varchar("+ 255 * weight +")", remarks: "描述")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用，1启用，0不启用") {
                constraints(nullable: "false")
            }
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
        addUniqueConstraint(columnNames: "template_code,template_version,tenant_id", tableName: "hexl_excel_template", constraintName: "hexl_excel_template_u1")
    }

    changeSet(id: '2019-12-18-webexcel-hexl_excel_template_callbackUrl', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }

        addColumn(tableName: "hexl_excel_template") {
            column(name: "callback_url", type: "varchar(" + 480 * weight + ")", remarks: "回调地址", afterColumn: "enabled_flag")
        }
    }
}