package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_account_balance.groovy') {
    changeSet(author: "aaron.yi", id: "2020-02-16_hchg_account_balance") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_account_balance_S', startValue: "10001")
        }
        createTable(tableName: "hchg_account_balance", remarks: "账户余额") {
            column(name: "balance_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_account_balance_PK")
            }
            column(name: "account_num", type: "varchar(" + 60 * weight + ")", remarks: "账户编码") {
                constraints(nullable: "false")
            }
            column(name: "balance_amount", type: "decimal(20,2)", defaultValue: "0", remarks: "账户余额") {
                constraints(nullable: "false")
            }
            column(name: "account_type", type: "varchar(" + 10 * weight + ")", remarks: "账户类型，编码：HCHG.ACCOUNT.ACCOUNT_TYPE(USER,TENANT)") {
                constraints(nullable: "false")
            }
            column(name: "account_id", type: "bigint", remarks: "账户id") {
                constraints(nullable: "false")
            }
            column(name: "account_name", type: "varchar(" + 240 * weight + ")", remarks: "账户名称") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识") {
                constraints(nullable: "false")
            }
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
        addUniqueConstraint(columnNames: "account_num,tenant_id", tableName: "hchg_account_balance", constraintName: "hchg_account_balance_U1")
        createIndex(tableName: "hchg_account_balance", indexName: "hchg_account_balance_N1") {
            column(name: "account_name")
            column(name: "tenant_id")
        }
        addUniqueConstraint(columnNames: "account_type,account_id,tenant_id", tableName: "hchg_account_balance", constraintName: "hchg_account_balance_U2")
    }
}