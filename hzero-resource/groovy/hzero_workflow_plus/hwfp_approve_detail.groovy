package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_approve_detail.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-09-hwfp_approve_detail") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwfp_approve_detail_s', startValue: "1")
        }
        createTable(tableName: "hwfp_approve_detail") {
            column(name: "approve_detail_id", type: "bigint", autoIncrement: true, startWith: "10001", remarks: "主键") {
                constraints(nullable: false, primaryKey: true, primaryKeyName: "hwfp_approve_detail_PK")
            }
            column(name: "approve_chain_id", type: "bigint", remarks: "审批链头ID") {
                constraints(nullable: false)
            }
            column(name: "approve_chain_line_id", type: "bigint", remarks: "审批链行ID") {
                constraints(nullable: false)
            }
            column(name: "form_key", type: "varchar(" + 225 * weight + ")", remarks: "表单KEY")
            column(name: "approve_rule", type: "bigint", remarks: "审批规则") {
                constraints(nullable: false)
            }
            column(name: "assignee", type: "varchar(" + 225 * weight + ")", remarks: "审批者")
            column(name: "enabled_condition", type: "bigint", remarks: "启用条件（服务任务，跳转条件）")
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "禁用启用标示") {
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
    }
}