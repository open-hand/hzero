package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_account_recharge.groovy') {
    changeSet(author: "aaron.yi", id: "2020-02-16_hchg_account_recharge") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_account_recharge_S', startValue: "10001")
        }
        createTable(tableName: "hchg_account_recharge", remarks: "账户充值记录") {
            column(name: "recharge_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_account_recharge_PK")
            }
            column(name: "balance_id", type: "bigint", remarks: "账户余额信息ID，hchg_account_balance.balance_id") {
                constraints(nullable: "false")
            }
            column(name: "request_time", type: "datetime", remarks: "充值发起时间") {
                constraints(nullable: "false")
            }
            column(name: "recharged_time", type: "datetime", remarks: "充值完成时间")
            column(name: "recharge_channel", type: "varchar(" + 240 * weight + ")", remarks: "充值渠道") {
                constraints(nullable: "false")
            }
            column(name: "transaction_serial", type: "varchar(" + 150 * weight + ")", remarks: "交易流水号")
            column(name: "recharge_amount", type: "decimal(20,2)", remarks: "充值金额") {
                constraints(nullable: "false")
            }
            column(name: "balance_amount", type: "decimal(20,2)", remarks: "充值后余额")
            column(name: "recharge_status", type: "varchar(" + 30 * weight + ")", defaultValue: "PROCESSING", remarks: "充值状态,[PROCESSING,SUCCESS,ERROR],HCHG.ACCOUNT.RECHARGE.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注")
            column(name: "process_message", type: "varchar(" + 1000 * weight + ")", remarks: "处理信息")
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
        createIndex(tableName: "hchg_account_recharge", indexName: "hchg_account_recharge_N1") {
            column(name: "transaction_serial")
            column(name: "tenant_id")
        }
    }
}