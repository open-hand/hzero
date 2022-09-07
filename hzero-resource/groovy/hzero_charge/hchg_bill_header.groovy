package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_bill_header.groovy') {
    changeSet(author: "aaron.yi", id: "2020-02-15_hchg_bill_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_bill_header_S', startValue: "10001")
        }
        createTable(tableName: "hchg_bill_header", remarks: "账单头") {
            column(name: "header_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_bill_header_PK")
            }
            column(name: "bill_num", type: "varchar(" + 60 + ")", remarks: "账单编号") {
                constraints(nullable: "false")
            }
            column(name: "bill_name", type: "varchar(" + 240 * weight + ")", remarks: "账单名称") {
                constraints(nullable: "false")
            }
            column(name: "bill_date", type: "Date", remarks: "账单日期") {
                constraints(nullable: "false")
            }
            column(name: "amount", type: "decimal(20,2)", remarks: "账单金额") {
                constraints(nullable: "false")
            }
            column(name: "status_code", type: "varchar(" + 30 + ")", defaultValue: "BILLED", remarks: "状态,值集:HCHG.BILL.STATUS") {
                constraints(nullable: "false")
            }
            column(name: "rule_code", type: "varchar(" + 30 + ")", remarks: "计费规则代码") {
                constraints(nullable: "false")
            }
            column(name: "rule_name", type: "varchar(" + 240 * weight + ")", remarks: "计费规则名称") {
                constraints(nullable: "false")
            }
            column(name: "discount_total", type: "decimal(20,2)", remarks: "总折扣额度")
            column(name: "bill_entity_type", type: "varchar(" + 30 + ")", remarks: "账单发生实体,值集:HCHG.BILL.ENTITY_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "bill_entity_id", type: "bigint", remarks: "账单实体主键") {
                constraints(nullable: "false")
            }
            column(name: "bill_entity_name", type: "varchar(" + 240 * weight + ")", remarks: "账单实体名称") {
                constraints(nullable: "false")
            }
            column(name: "currency_code", type: "varchar(" + 3 + ")", remarks: "币种") {
                constraints(nullable: "false")
            }
            column(name: "transaction_serial", type: "varchar(" + 150 + ")", remarks: "交易流水号")
            column(name: "payment_status", type: "varchar(" + 30 + ")", defaultValue: "UNPAID", remarks: "支付状态") {
                constraints(nullable: "false")
            }
            column(name: "actual_payment_time", type: "datetime", remarks: "实际支付时间")
            column(name: "actual_payment_amount", type: "decimal(20,2)", remarks: "实际支付金额")
            column(name: "source_system_id", type: "bigint", remarks: "来源系统ID") {
                constraints(nullable: "false")
            }
            column(name: "source_bill_num", type: "varchar(" + 100 + ")", remarks: "来源账单号，在同一个来源系统下唯一") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 360 * weight + ")", remarks: "备注说明")
            column(name:"process_message",type:"varchar("+ 1000 * weight +")",remarks:"处理信息")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "bill_num,tenant_id", tableName: "hchg_bill_header", constraintName: "hchg_bill_header_U1")
        addUniqueConstraint(columnNames: "bill_name,tenant_id", tableName: "hchg_bill_header", constraintName: "hchg_bill_header_U2")
        createIndex(tableName: "hchg_bill_header", indexName: "hchg_bill_header_N1") {
            column(name: "bill_date")
        }
        addUniqueConstraint(columnNames: "source_system_id,source_bill_num,tenant_id", tableName: "hchg_bill_header", constraintName: "hchg_bill_header_U3")
    }
}