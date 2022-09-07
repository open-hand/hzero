package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_structure_field_header.groovy') {

    changeSet(author: "aaron.yi", id: "2020-04-01_hitf_structure_field_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_structure_field_header_S', startValue: "10001")
        }
        createTable(tableName: "hitf_structure_field_header", remarks: "结构字段头") {
            column(name: "header_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_structure_field_header_PK")
            }
            column(name: "structure_num", type: "varchar(" + 100 + ")", remarks: "结构编码") {
                constraints(nullable: "false")
            }
            column(name: "structure_name", type: "varchar(" + 255 * weight + ")", remarks: "结构名称") {
                constraints(nullable: "false")
            }
            column(name: "structure_category", type: "varchar(" + 30 + ")", remarks: "结构分类，HITF.STRUCTURE_CATEGORY") {
                constraints(nullable: "false")
            }
            column(name: "structure_desc", type: "varchar(" + 255 * weight + ")", remarks: "结构说明")
            column(name: "biz_usage", type: "varchar(" + 30 + ")", remarks: "业务用途，HITF.STRCTURE.LINE.BIZ_USAGE") {
                constraints(nullable: "false")
            }
            column(name: "composition", type: "varchar(" + 30 + ")", remarks: "构建方式，HITF.STRCTURE.COMPOSITION") {
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
        addUniqueConstraint(columnNames: "structure_num,tenant_id", tableName: "hitf_structure_field_header", constraintName: "hitf_structure_field_header_U1")
        addUniqueConstraint(columnNames: "structure_name,tenant_id", tableName: "hitf_structure_field_header", constraintName: "hitf_structure_field_header_U2")
    }
}