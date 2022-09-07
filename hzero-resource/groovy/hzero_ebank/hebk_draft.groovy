package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_draft.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-02_hebk_draft") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_draft_s', startValue: "10001")
        }
        createTable(tableName: "hebk_draft", remarks: "票据") {
            column(name: "draft_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_draft_pk")
            }
            column(name: "bank_mark", type: "varchar(" + 30 * weight + ")", remarks: "银行标识，HEBK.BANK_MARK") {
                constraints(nullable: "false")
            }
            column(name: "bank_code", type: "varchar(" + 30 * weight + ")", remarks: "联行号")
            column(name: "account_number", type: "varchar(" + 60 * weight + ")", remarks: "帐号") {
                constraints(nullable: "false")
            }
            column(name: "draft_type", type: "varchar(" + 30 * weight + ")", remarks: "票据类别，HEBK.DRAFT_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "draft_number", type: "varchar(" + 30 * weight + ")", remarks: "票据号码") {
                constraints(nullable: "false")
            }
            column(name: "organization_code", type: "varchar(" + 30 * weight + ")", remarks: "组织机构代码")
            column(name: "amount", type: "decimal(18,2)", remarks: "票据金额") {
                constraints(nullable: "false")
            }
            column(name: "date", type: "datetime", remarks: "出票日期") {
                constraints(nullable: "false")
            }
            column(name: "draft_status_code", type: "varchar(" + 30 * weight + ")", remarks: "票据状态，HEBK.DRAFT_STATUS") {
                constraints(nullable: "false")
            }
            column(name: "bank_draft_status_code", type: "varchar(" + 30 * weight + ")", remarks: "银行票据状态") {
                constraints(nullable: "false")
            }
            column(name: "bank_draft_status_desc", type: "varchar(" + 240 * weight + ")", remarks: "银行票据状态说明")
            column(name: "due_date", type: "datetime", remarks: "到期日") {
                constraints(nullable: "false")
            }
            column(name: "drawer_name", type: "varchar(" + 120 * weight + ")", remarks: "出票人名称")
            column(name: "acceptor_name", type: "varchar(" + 120 * weight + ")", remarks: "承兑人名称")
            column(name: "payee_name", type: "varchar(" + 120 * weight + ")", remarks: "收款人名称")
            column(name: "applicant_name", type: "varchar(" + 120 * weight + ")", remarks: "申请人名称")
            column(name: "drawer_account_number", type: "varchar(" + 60 * weight + ")", remarks: "出票人账号")
            column(name: "acceptor_account_number", type: "varchar(" + 60 * weight + ")", remarks: "承兑人账号")
            column(name: "payee_account_number", type: "varchar(" + 60 * weight + ")", remarks: "收款人账号")
            column(name: "transfer_flag", type: "bigint", defaultValue: "0", remarks: "能否转让标识")
            column(name: "remark", type: "varchar(" + 600 * weight + ")", remarks: "备注")
            column(name: "consign_flag", type: "tinyint", defaultValue: "0", remarks: "到期无条件支付委托")
            column(name: "auto_draft_flag", type: "varchar(" + 30 * weight + ")", remarks: "自动出票标识")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}