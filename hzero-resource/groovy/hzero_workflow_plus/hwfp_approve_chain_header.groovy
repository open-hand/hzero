package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_approve_chain_header.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-10-hwfp_approve_chain_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwfp_approve_chain_header_s', startValue: "1")
        }
        createTable(tableName: "hwfp_approve_chain_header") {
            column(name: "approve_chain_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "主键") {
                constraints(nullable: false, primaryKey: true, primaryKeyName: "hwfp_approve_chain_header_PK")
            }
            column(name: "process_key", type: "varchar(" + 150 * weight + ")", remarks: "流程定义ID") {
                constraints(nullable: false)
            }
            column(name: "user_task_id", type: "varchar(" + 225 * weight + ")", remarks: "任务ID") {
                constraints(nullable: false)
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "禁用启用标示") {
                constraints(nullable: false)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "版本号") {
                constraints(nullable: false)
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: false) }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: false) }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: false) }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: false) }
        }
        addUniqueConstraint(columnNames: "process_key,user_task_id,tenant_id", tableName: "hwfp_approve_chain_header", constraintName: "hwfp_approve_chain_header_u1")
    }
}