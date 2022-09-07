package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_draft_trade_business.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-02_hebk_draft_trade_business") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_draft_trade_business_s', startValue: "10001")
        }
        createTable(tableName: "hebk_draft_trade_business", remarks: "票据交易记录") {
            column(name: "business_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_draft_trade_business_pk")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "draft_id", type: "bigint", remarks: "票据ID，hebk_draft.draft_id")
            column(name: "draft_number", type: "varchar(" + 30 * weight + ")", remarks: "票据号码")
            column(name: "bank_code", type: "varchar(" + 30 * weight + ")", remarks: "联行号")
            column(name: "account_number", type: "varchar(" + 60 * weight + ")", remarks: "帐号") {
                constraints(nullable: "false")
            }
            column(name: "status_code", type: "varchar(" + 30 * weight + ")", remarks: "状态，HEBK.DRAFT_TRADE_BIZ.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "bank_status_code", type: "varchar(" + 30 * weight + ")", remarks: "银行状态")
            column(name: "bank_status_desc", type: "varchar(" + 240 * weight + ")", remarks: "银行状态说明")
            column(name: "bank_transaction_id", type: "varchar(" + 30 * weight + ")", remarks: "银行交易流水号") {
                constraints(nullable: "false")
            }
            column(name: "failure_reason", type: "varchar(" + 240 * weight + ")", remarks: "失败原因")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "hebk_draft_trade_business", indexName: "hebk_draft_trade_business_n1") {
            column(name: "tenant_id")
            column(name: "status_code")
        }
    }
}