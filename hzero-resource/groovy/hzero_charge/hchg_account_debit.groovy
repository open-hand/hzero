package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_account_debit.groovy') {
    changeSet(author: "bo.he02@hand-china.com", id: "2020-02-27_hchg_account_debit") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_account_debit_S', startValue: "10001")
        }
        createTable(tableName: "hchg_account_debit", remarks: "账户扣款记录") {
            column(name: "debit_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_account_debit_PK")
            }
            column(name: "balance_id", type: "bigint", remarks: "账户余额信息ID，hchg_account_balance.balance_id") {
                constraints(nullable: "false")
            }
            column(name: "debit_time", type: "datetime", remarks: "扣款时间") {
                constraints(nullable: "false")
            }
            column(name: "cost_name", type: "varchar(" + 240 * weight + ")", remarks: "费用名称")
            column(name: "transaction_serial", type: "varchar(" + 150 * weight + ")", remarks: "交易流水号")
            column(name: "debit_amount", type: "decimal(20,2)", remarks: "扣款金额") {
                constraints(nullable: "false")
            }
            column(name: "balance_amount", type: "decimal(20,2)", remarks: "账户余额") {
                constraints(nullable: "false")
            }
            column(name: "business_code", type: "varchar(" + 60 * weight + ")", remarks: "业务类型码：用于区分不同的业务类型，由业务系统控制(每个业务场景应定义自己唯一的业务类型码)") {
                constraints(nullable: "false")
            }
            column(name: "business_key", type: "varchar(" + 60 * weight + ")", remarks: "业务唯一键：在相同业务类型码下，业务唯一键不能重复，由业务系统控制") {
                constraints(nullable: "false")
            }
            column(name: "debit_status", type: "varchar(" + 30 * weight + ")", remarks: "扣款状态,[SUCCESS,FAILURE],HCHG.ACCOUNT.DEBIT_STATUS") {
                constraints(nullable: "false")
            }
            column(name: "process_message", type: "varchar(" + 1000 * weight + ")", remarks: "处理信息")
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注")
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
        addUniqueConstraint(columnNames: "transaction_serial,tenant_id", tableName: "hchg_account_debit", constraintName: "hchg_account_debit_U1")
        addUniqueConstraint(columnNames: "balance_id,business_code,business_key", tableName: "hchg_account_debit", constraintName: "hchg_account_debit_U2")

    }
}