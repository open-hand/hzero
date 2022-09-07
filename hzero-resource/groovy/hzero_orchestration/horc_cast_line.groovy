package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_cast_line.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-07_horc_cast_line") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_cast_line_S', startValue: "10001")
        }
        createTable(tableName: "horc_cast_line", remarks: "数据转化配置行表") {
            column(name: "cast_line_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_cast_line_PK")
            }
            column(name: "cast_header_id", type: "bigint", remarks: "转化头ID") {
                constraints(nullable: "false")
            }
            column(name: "cast_type", type: "varchar(30)", remarks: "数据转换类型，HORC.CAST_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "cast_root", type: "varchar(240)", remarks: "字段路径")
            column(name: "cast_field", type: "varchar(240)", remarks: "字段名称") {
                constraints(nullable: "false")
            }
            column(name: "cast_expr", type: "clob", remarks: "转化表达式")
            column(name: "cast_sql", type: "clob", remarks: "转化SQL")
            column(name: "cast_lov_code", type: "varchar(80)", remarks: "转化LOV")
            column(name: "cast_lov_field", type: "varchar(240)", remarks: "LOV转化字段")
            column(name: "cast_lov_lang", type: "varchar(30)", defaultValue: "zh_CN", remarks: "LOV转化语言")
            column(name: "status_code", type: "varchar(30)", defaultValue: "NEW", remarks: "状态") {
                constraints(nullable: "false")
            }
            column(name: "version", type: "decimal(8,4)", defaultValue: "0", remarks: "版本")
            column(name: "from_version", type: "decimal(8,4)", remarks: "来源版本")
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
        addUniqueConstraint(columnNames: "cast_header_id,cast_root,cast_field", tableName: "horc_cast_line", constraintName: "horc_cast_line_U1")
        createIndex(tableName: "horc_cast_line", indexName: "horc_cast_line_N1") {
            column(name: "cast_header_id")
        }
    }
}