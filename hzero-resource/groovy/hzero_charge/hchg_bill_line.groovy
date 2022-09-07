package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_bill_line.groovy') {
    changeSet(author: "aaron.yi", id: "2020-02-15_hchg_bill_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_bill_line_S', startValue: "10001")
        }
        createTable(tableName: "hchg_bill_line", remarks: "账单行") {
            column(name: "line_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_bill_line_PK")
            }
            column(name: "header_id", type: "bigint", remarks: "账单头，hchg_bill_header.header_id") {
                constraints(nullable: "false")
            }
            column(name: "line_num", type: "bigint", remarks: "行号") {
                constraints(nullable: "false")
            }
            column(name: "issue_date_start", type: "datetime", remarks: "发生日期从") {
                constraints(nullable: "false")
            }
            column(name: "issue_date_end", type: "datetime", remarks: "发生日期至") {
                constraints(nullable: "false")
            }
            column(name: "uom", type: "varchar(" + 30 * weight + ")", remarks: "计量单位,值集:HCHG.BILL.ISSUE_UOM") {
                constraints(nullable: "false")
            }
            column(name: "value", type: "decimal(30,4)", remarks: "计量值") {
                constraints(nullable: "false")
            }
            column(name: "discount_type", type: "varchar(" + 30 * weight + ")", remarks: "优惠类型")
            column(name: "discount_amount", type: "decimal(20,2)", remarks: "优惠额度")
            column(name: "issue_amount", type: "decimal(20,2)", remarks: "发生金额") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 500 * weight + ")", remarks: "备注说明")
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
        addUniqueConstraint(columnNames: "header_id,line_num,tenant_id", tableName: "hchg_bill_line", constraintName: "hchg_bill_line_U1")

    }
}