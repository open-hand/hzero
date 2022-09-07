package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_automatic_process.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-09-hwfp_automatic_process") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwfp_automatic_process_s', startValue: "1")
        }
        createTable(tableName: "hwfp_automatic_process") {
            column(name: "automatic_id", type: "bigint", autoIncrement: true, startWith: "10001", remarks: "主键") {
                constraints(nullable: false, primaryKey: true, primaryKeyName: "hwfp_automatic_process_PK")
            }
            column(name: "process_key", type: "varchar(" + 150 * weight + ")", remarks: "流程定义ID") {
                constraints(nullable: false)
            }
            column(name: "process_condition", type: "varchar(" + 20 * weight + ")", remarks: "处理条件") {
                constraints(nullable: false)
            }
            column(name: "process_rule", type: "varchar(" + 60 * weight + ")", remarks: "处理规则") {
                constraints(nullable: false)
            }
            column(name: "process_start_date", type: "datetime(0)", remarks: "开始日期")
            column(name: "process_end_date", type: "datetime(0)", remarks: "结束日期")
            column(name: "employee_code", type: "varchar(" + 60 * weight + ")", remarks: "当前用户") {
                constraints(nullable: false)
            }
            column(name: "delegate_code", type: "varchar(" + 60 * weight + ")", remarks: "转交的员工")
            column(name: "timeout_value", type: "int", remarks: "超时时间")
            column(name: "timeout_unit", type: "varchar(" + 15 * weight + ")", remarks: "超时单位")
            column(name: "process_remark", type: "varchar(" + 225 * weight + ")", remarks: "处理意见")
            column(name: "enabled_flag", type: "Tinyint", remarks: "禁用启用标示") {
                constraints(nullable: false)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "版本号") {
                constraints(nullable: false)
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: " false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: " false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: " false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: " false") }
        }
        addUniqueConstraint(columnNames: "process_key,employee_code,tenant_id", tableName: "hwfp_automatic_process", constraintName: "hwfp_automatic_process_u1")
    }
}