package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_cast_header.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-07_horc_cast_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_cast_header_S', startValue: "10001")
        }
        createTable(tableName: "horc_cast_header", remarks: "数据转化配置头表") {
            column(name: "cast_header_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_cast_header_PK")
            }
            column(name: "cast_code", type: "varchar(" + 400 * weight + ")", remarks: "转化编码") {
                constraints(nullable: "false")
            }
            column(name: "cast_name", type: "varchar(" + 400 * weight + ")", remarks: "转化名称") {
                constraints(nullable: "false")
            }
            column(name: "data_type", type: "varchar(30)", remarks: "转化数据类型，HORC.CAST_DATA_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识")
            column(name: "status_code", type: "varchar(30)", defaultValue: "NEW", remarks: "状态") {
                constraints(nullable: "false")
            }
            column(name: "version", type: "decimal(8,4)", defaultValue: "0", remarks: "版本") {
                constraints(nullable: "false")
            }
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
        addUniqueConstraint(columnNames: "tenant_id,cast_code", tableName: "horc_cast_header", constraintName: "horc_cast_header_U1")
    }
}


