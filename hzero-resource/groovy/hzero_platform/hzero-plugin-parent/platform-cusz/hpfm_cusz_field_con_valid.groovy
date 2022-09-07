package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_field_con_valid.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-04-09-hpfm_cusz_field_con_valid") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_field_con_valid_S', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_field_con_valid") {
            column(name: "con_valid_id", type: "bigint", autoIncrement: "true", startWith: "1", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hpfm_cusz_field_con_valid_PK")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "con_header_id", type: "bigint", remarks: "条件头表主键id") {
                constraints(nullable: "false")
            }
            column(name: "con_expression", type: "varchar(" + 255 * weight + ")", remarks: "条件逻辑表达式") {
                constraints(nullable: "false")
            }
            column(name: "error_message", type: "varchar(" + 255 * weight + ")", remarks: "错误信息") {
                constraints(nullable: "false")
            }
            column(name: "con_code", type: "int", remarks: "条件编码") {
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
        addUniqueConstraint(columnNames: "con_header_id,con_code,tenant_id", tableName: "hpfm_cusz_field_con_valid", constraintName: "hpfm_con_valid_U1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-04-10-hpfm_cusz_field_con_valid_tl") {
        createTable(tableName: "hpfm_cusz_field_con_valid_tl") {
            column(name: "con_valid_id", type: "bigint", remarks: "hpfm_cusz_field_con_valid 主键") { constraints(nullable: false) }
            column(name: "lang", type: "varchar(" + 30 * weight + ")", remarks: "语言") { constraints(nullable: false) }
            column(name: "error_message", type: "varchar(" + 510 * weight + ")", remarks: "错误信息描述") { constraints(nullable: false) }
        }
        addUniqueConstraint(columnNames: "con_valid_id,lang", tableName: "hpfm_cusz_field_con_valid_tl", constraintName: "hpfm_con_valid_tl_U1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-09-14-hpfm_cusz_field_con_valid-addColumn") {
        addColumn(tableName: "hpfm_cusz_field_con_valid") {
            column(name: "value", type: "varchar(" + 120 * weight + ")")
        }
    }

}
