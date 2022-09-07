package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_field_con_header.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-05-hpfm_cusz_field_con_header") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_field_con_header_S', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_field_con_header") {
            column(name: "con_header_id", type: "bigint", autoIncrement: "true", startWith: "1", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hpfm_cusz_field_con_header_PK")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "config_field_id", type: "bigint", remarks: "条件关联字段id") {
                constraints(nullable: "false")
            }
            column(name: "con_type", type: "varchar(" + 30 * weight + ")", remarks: "条件类型，显示、隐藏、必输") {
                constraints(nullable: "false")
            }
            column(name: "con_expression", type: "varchar(" + 255 * weight + ")", remarks: "条件逻辑表达式") {
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
        addUniqueConstraint(columnNames: "config_field_id,con_type,tenant_id", tableName: "hpfm_cusz_field_con_header", constraintName: "hpfm_cusz_field_con_header_U1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-04-10-hpfm_cusz_field_con_header-modify-defaultValue") {
        dropDefaultValue(tableName: "hpfm_cusz_field_con_header", columnName: "con_expression", columnDataType: "varchar")
        addDefaultValue(tableName: "hpfm_cusz_field_con_header", columnName: "con_expression", columnDataType: "varchar", defaultValue: "1")
    }

}