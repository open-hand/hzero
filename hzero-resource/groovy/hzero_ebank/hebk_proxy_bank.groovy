package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_proxy_bank.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_proxy_bank") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_proxy_bank_s', startValue: "10001")
        }
        createTable(tableName: "hebk_proxy_bank", remarks: "代理银行") {
            column(name: "bank_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_proxy_bank_pk")
            }
            column(name: "proxy_id", type: "bigint", remarks: "代理ID，hebk_proxy.proxy_id") {
                constraints(nullable: "false")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "tenant_id,bank_mark", tableName: "hebk_proxy_bank", constraintName: "hebk_proxy_bank_u1")
    }
}