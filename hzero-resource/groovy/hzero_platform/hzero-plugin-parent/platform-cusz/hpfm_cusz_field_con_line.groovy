package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_field_con_line.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-05-hpfm_cusz_field_con_line") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_field_con_line_S', startValue: "10001")
        }
        createTable(tableName: "hpfm_cusz_field_con_line") {
            column(name: "con_line_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hpfm_cusz_field_con_line_PK")
            }
            column(name: "con_header_id", type: "bigint", remarks: "条件头表主键") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户id") {
                constraints(nullable: "false")
            }
            column(name: "con_code", type: "int", remarks: "条件编码") {
                constraints(nullable: "false")
            }
            column(name: "source_unit_id", type: "bigint", remarks: "源字段所属单元id") {
                constraints(nullable: "false")
            }
            column(name: "source_model_id", type: "bigint", remarks: "源字段所属模型id") {
                constraints(nullable: "false")
            }
            column(name: "source_field_id", type: "bigint", remarks: "源字段id") {
                constraints(nullable: "false")
            }
            column(name: "con_expression", type: "varchar(" + 12 * weight + ")", remarks: "条件运算符") {
                constraints(nullable: "false")
            }
            column(name: "target_type", type: "varchar(" + 30 * weight + ")", remarks: "目标字段类型，本单元、固定值")
            column(name: "target_unit_id", type: "bigint", remarks: "目标字段所属单元id")
            column(name: "target_model_id", type: "bigint", remarks: "目标字段所属模型id")
            column(name: "target_field_id", type: "bigint", remarks: "目标字段id")
            column(name: "target_value", type: "varchar(" + 225 * weight + ")", remarks: "目标字段值")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "con_header_id,con_code,tenant_id", tableName: "hpfm_cusz_field_con_line", constraintName: "hpfm_cusz_field_con_line_U1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-14-hpfm_cusz_field_con_line") {
        addColumn(tableName: 'hpfm_cusz_field_con_line') {
            column(name: "target_value_meaning", type: "varchar(" + 225 * weight + ")", remarks: "值集翻译")
        }
    }
}