package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_structure_field_line.groovy') {

    changeSet(author: "aaron.yi", id: "2020-04-01_hitf_structure_field_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_structure_field_line_S', startValue: "10001")
        }
        createTable(tableName: "hitf_structure_field_line", remarks: "结构字段行") {
            column(name: "line_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_structure_field_line_PK")
            }
            column(name: "header_id", type: "bigint", remarks: "头表主键，hitf_structure_field_header.header_id") {
                constraints(nullable: "false")
            }
            column(name: "field_type", type: "varchar(" + 30 + ")", remarks: "字段类型，HITF.STRCTURE.LINE.FILED_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "field_name", type: "varchar(" + 255 * weight + ")", remarks: "字段名称") {
                constraints(nullable: "false")
            }
            column(name: "field_desc", type: "varchar(" + 255 * weight + ")", remarks: "字段描述")
            column(name: "seq_num", type: "bigint", defaultValue: "10", remarks: "参数顺序") {
                constraints(nullable: "false")
            }
            column(name: "format_mask", type: "varchar(" + 150 * weight + ")", remarks: "格式掩码，用来格式化输出")
            column(name: "default_value", type: "varchar(" + 255 * weight + ")", remarks: "默认值")
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "parent_id", type: "bigint", remarks: "父健")
            column(name: "level_path", type: "clob", remarks: "层级") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户") {
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
        addUniqueConstraint(columnNames: "field_name,header_id,tenant_id", tableName: "hitf_structure_field_line", constraintName: "hitf_structure_field_line_U1")
    }
}