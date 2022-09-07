package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_cast_line_his.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-08-17_horc_cast_line_his") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_cast_line_his_S', startValue: "10001")
        }
        createTable(tableName: "horc_cast_line_his", remarks: "数据映射配置行历史") {
            column(name: "cast_line_his_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_cast_line_his_PK")
            }
            column(name: "cast_line_id", type: "bigint", remarks: "转化行ID") {
                constraints(nullable: "false")
            }
            column(name: "cast_header_id", type: "bigint", remarks: "转化头ID") {
                constraints(nullable: "false")
            }
            column(name: "cast_type", type: "varchar(30)", remarks: "数据转换类型，HORC.CAST_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "cast_root", type: "varchar(240)", remarks: "转化根节点")
            column(name: "cast_field", type: "varchar(240)", remarks: "转化字段") {
                constraints(nullable: "false")
            }
            column(name: "cast_expr", type: "clob", remarks: "转化表达式")
            column(name: "cast_sql", type: "clob", remarks: "转化SQL")
            column(name: "cast_lov_code", type: "varchar(80)", remarks: "转化LOV")
            column(name: "cast_lov_field", type: "varchar(240)", remarks: "LOV转化字段")
            column(name: "cast_lov_lang", type: "varchar(30)", defaultValue: "zh_CN", remarks: "LOV转化语言")
            column(name: "version", type: "decimal(8,4)", remarks: "版本") {
                constraints(nullable: "false")
            }
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
    }
}