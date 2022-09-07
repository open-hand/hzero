package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_bank_account.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-16_hebk_bank_account") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_bank_account_s', startValue: "10001")
        }
        createTable(tableName: "hebk_bank_account", remarks: "银行账户") {
            column(name: "account_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_bank_account_pk")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "account_number", type: "varchar(" + 60 * weight + ")", remarks: "帐号") {
                constraints(nullable: "false")
            }
            column(name: "bank_code", type: "varchar(" + 30 * weight + ")", remarks: "联行号")
            column(name: "bank_number", type: "varchar(" + 10 * weight + ")", remarks: "账户所属银行号")
            column(name: "account_name", type: "varchar(" + 240 * weight + ")", remarks: "账户名称")
            column(name: "currency_code", type: "varchar(" + 10 * weight + ")", remarks: "货币")
            column(name: "group_number", type: "varchar(" + 30 * weight + ")", remarks: "账户组编号")
            column(name: "group_product_type", type: "varchar(" + 30 * weight + ")", remarks: "账户组产品类型，HEBK.BANK_ACCOUNT_GPT")
            column(name: "branch_id", type: "varchar(" + 10 * weight + ")", remarks: "开户行机构号")
            column(name: "bank_name", type: "varchar(" + 160 * weight + ")", remarks: "开户行名称")
            column(name: "children_flag", type: "tinyint", defaultValue: "0", remarks: "是否有子账户") {
                constraints(nullable: "false")
            }
            column(name: "parent_account_id", type: "bigint", remarks: "父账户ID，hebk_bank_account.account_id")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "tenant_id,bank_mark,account_number", tableName: "hebk_bank_account", constraintName: "hebk_bank_account_u1")
    }
}