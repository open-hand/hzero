package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_fund_receipt.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-28_hebk_fund_receipt") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_fund_receipt_s', startValue: "10001")
        }
        createTable(tableName: "hebk_fund_receipt", remarks: "金融类回单") {
            column(name: "receipt_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_fund_receipt_pk")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "transfer_instruction_id", type: "varchar(" + 30 * weight + ")", remarks: "转账指令ID")
            column(name: "file_name", type: "varchar(" + 60 * weight + ")", remarks: "文件名称")
            column(name: "status_code", type: "varchar(" + 30 * weight + ")", remarks: "状态，HEBK.FUND_RECEIPT.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}