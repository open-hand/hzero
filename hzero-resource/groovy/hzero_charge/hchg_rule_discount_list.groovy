package script.table

databaseChangeLog(logicalFilePath: 'script/db/hchg_rule_discount_list.groovy') {

    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-02-14_hchg_rule_discount_list") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_rule_discount_list_S', startValue: "10001")
        }
        createTable(tableName: "hchg_rule_discount_list", remarks: '计费规则优惠范围表') {
            column(name: "discount_list_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_rule_discount_list_PK")
            }
            column(name: "rule_header_id", type: "bigint", remarks: "计费规则头ID，hchg_rule_header表主键") {
                constraints(nullable: "false")
            }
            column(name: "rule_discount_id", type: "bigint", remarks: "计费规则优惠ID，hchg_rule_discount表主键") {
                constraints(nullable: "false")
            }
            column(name: "type_code", type: "varchar(" + 30 * weight + ")", remarks: "类型代码，编码：HCHG.DISCOUNT.EFFECTIVE_RANGE") {
                constraints(nullable: "false")
            }
            column(name: "value_id", type: "bigint", remarks: "主键ID") {
                constraints(nullable: "false")
            }
            column(name: "value_name", type: "varchar(" + 240 * weight + ")", remarks: "名称") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "clob(240)", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "0") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "0") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
        }
        addUniqueConstraint(columnNames: "rule_discount_id,type_code,value_id", tableName: "hchg_rule_discount_list", constraintName: "hchg_rule_discount_list_U1")

        createIndex(tableName: "hchg_rule_discount_list", indexName: "hchg_rule_discount_list_N1") {
            column(name: "rule_discount_id")
        }

    }


}