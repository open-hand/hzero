package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_fund_trade_business.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-02_hebk_fund_trade_business") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_fund_trade_business_s', startValue: "10001")
        }
        createTable(tableName: "hebk_fund_trade_business", remarks: "金融类交易记录") {
            column(name: "business_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_fund_trade_business_pk")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "bank_code", type: "varchar(" + 30 * weight + ")", remarks: "联行号")
            column(name: "account_number", type: "varchar(" + 60 * weight + ")", remarks: "帐号") {
                constraints(nullable: "false")
            }
            column(name: "reference_order", type: "varchar(" + 60 * weight + ")", remarks: "参考单据编号")
            column(name: "trade_amount", type: "decimal(20,2)", remarks: "交易金额") {
                constraints(nullable: "false")
            }
            column(name: "trade_date", type: "date", remarks: "交易日期") {
                constraints(nullable: "false")
            }
            column(name: "currency", type: "varchar(" + 30 * weight + ")", remarks: "交易货币") {
                constraints(nullable: "false")
            }
            column(name: "status_code", type: "varchar(" + 30 * weight + ")", remarks: "状态，HEBK.FUND_TRADE_BIZ.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "bank_status_code", type: "varchar(" + 30 * weight + ")", remarks: "银行状态")
            column(name: "bank_status_desc", type: "varchar(" + 240 * weight + ")", remarks: "银行状态说明")
            column(name: "trade_bank_code", type: "varchar(" + 30 * weight + ")", remarks: "交易人联行号")
            column(name: "trade_account_number", type: "varchar(" + 60 * weight + ")", remarks: "交易人账号") {
                constraints(nullable: "false")
            }
            column(name: "trade_account_name", type: "varchar(" + 240 * weight + ")", remarks: "交易人名称")
            column(name: "trade_account_bank_name", type: "varchar(" + 160 * weight + ")", remarks: "交易人开户行名称")
            column(name: "opposite_bank_code", type: "varchar(" + 30 * weight + ")", remarks: "交易对方联行号")
            column(name: "opposite_account_number", type: "varchar(" + 60 * weight + ")", remarks: "交易对方账号") {
                constraints(nullable: "false")
            }
            column(name: "opposite_account_name", type: "varchar(" + 240 * weight + ")", remarks: "交易对方名称") {
                constraints(nullable: "false")
            }
            column(name: "opposite_account_bank_name", type: "varchar(" + 160 * weight + ")", remarks: "交易对方开户行名称")
            column(name: "agent_bank_code", type: "varchar(" + 30 * weight + ")", remarks: "被代理联行号")
            column(name: "agent_account_number", type: "varchar(" + 60 * weight + ")", remarks: "被代理账号")
            column(name: "agent_account_name", type: "varchar(" + 240 * weight + ")", remarks: "被代理名称")
            column(name: "agent_account_bank_name", type: "varchar(" + 160 * weight + ")", remarks: "被代理开户行名称")
            column(name: "transaction_serial_number", type: "varchar(" + 30 * weight + ")", remarks: "交易流水号")
            column(name: "transaction_id", type: "varchar(" + 30 * weight + ")", remarks: "记录标识号")
            column(name: "transfer_instruction_id", type: "varchar(" + 30 * weight + ")", remarks: "转账指令ID")
            column(name: "bank_transaction_id", type: "varchar(" + 30 * weight + ")", remarks: "银行交易流水号")
            column(name: "account_balance", type: "decimal(20,2)", defaultValue: "0", remarks: "交易后余额")
            column(name: "available_balance", type: "decimal(20,2)", defaultValue: "0", remarks: "可用余额")
            column(name: "frozen_amount", type: "decimal(20,2)", defaultValue: "0", remarks: "冻结金额")
            column(name: "overdrawn_amount", type: "decimal(20,2)", defaultValue: "0", remarks: "透支额度")
            column(name: "available_overdrawn_amount", type: "decimal(20,2)", defaultValue: "0", remarks: "可用透支额度")
            column(name: "purpose", type: "varchar(" + 200 * weight + ")", remarks: "用途")
            column(name: "postscript", type: "varchar(" + 500 * weight + ")", remarks: "附言")
            column(name: "business_type_code", type: "varchar(" + 30 * weight + ")", remarks: "业务类型，HEBK.FUND_TRADE_BIZ.BIZ_TYPE")
            column(name: "bank_business_type", type: "varchar(" + 30 * weight + ")", remarks: "银行业务类型")
            column(name: "direction_code", type: "varchar(" + 30 * weight + ")", remarks: "来往账标识，HEBK.FUND_TRADE_BIZ.DIRECTION")
            column(name: "fee_account", type: "varchar(" + 60 * weight + ")", remarks: "费用账户")
            column(name: "fee_amount", type: "decimal(20,2)", defaultValue: "0", remarks: "费用金额")
            column(name: "fee_currency", type: "varchar(" + 10 * weight + ")", remarks: "费用货币")
            column(name: "interest_date", type: "date", remarks: "起息日期")
            column(name: "bank_voucher_type", type: "varchar(" + 30 * weight + ")", remarks: "银行凭证类型")
            column(name: "voucher_number", type: "varchar(" + 30 * weight + ")", remarks: "凭证号码")
            column(name: "exchange_rate", type: "decimal(20,2)", defaultValue: "0", remarks: "汇率")
            column(name: "receipt_url", type: "varchar(" + 150 * weight + ")", remarks: "电子回单附件路径")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "tenant_id,bank_mark,transaction_id", tableName: "hebk_fund_trade_business", constraintName: "hebk_fund_trade_business_u1")
        createIndex(tableName: "hebk_fund_trade_business", indexName: "hebk_fund_trade_business_n1") {
            column(name: "tenant_id")
            column(name: "status_code")
        }
    }
}