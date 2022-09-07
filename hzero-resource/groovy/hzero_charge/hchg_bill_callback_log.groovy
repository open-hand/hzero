package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_bill_callback_log.groovy') {
    changeSet(author: "bo.he02@hand-chian.com", id: "2020-03-03_hchg_bill_callback_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_bill_callback_log_S', startValue: "10001")
        }
        createTable(tableName: "hchg_bill_callback_log", remarks: "账单回调记录表") {
            column(name: "callback_log_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_bill_callback_log_PK")
            }
            column(name: "bill_header_id", type: "bigint", remarks: "账单头，hchg_bill_header.header_id") {
                constraints(nullable: "false")
            }
            column(name: "callback_url", type: "varchar(" + 240 * weight + ")", remarks: "回调url") {
                constraints(nullable: "false")
            }
            column(name: "callback_status", type: "varchar(" + 30 * weight + ")", remarks: "回调状态，编码：HCHG.BILL.CALLBACK_STATUS") {
                constraints(nullable: "false")
            }
            column(name: "callback_count", type: "bigint", defaultValue: "1", remarks: "回调次数") {
                constraints(nullable: "false")
            }
            column(name: "callback_time", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "回调时间") {
                constraints(nullable: "false")
            }
            column(name: "callback_message", type: "varchar(" + 1000 * weight + ")", remarks: "回调消息")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "bill_header_id,tenant_id", tableName: "hchg_bill_callback_log", constraintName: "hchg_bill_callback_log_U1")

    }

    changeSet(author: "bo.he02@hand-chian.com", id: "2020-07-07_hchg_bill_callback_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        
        modifyDataType(tableName: "hchg_bill_callback_log", columnName: "callback_message", newDataType: "varchar(" + 1000 * weight + ")")
    }
}