package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_cast_header_his.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-08-17_horc_cast_header_his") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_cast_header_his_S', startValue: "10001")
        }
        createTable(tableName: "horc_cast_header_his", remarks: "数据映射配置头历史") {
            column(name: "cast_header_his_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_cast_header_his_PK")
            }
            column(name: "cast_header_id", type: "bigint", remarks: "转化ID") {
                constraints(nullable: "false")
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


