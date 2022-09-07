package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_dm_source.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-28_horc_dm_source") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_dm_source_S', startValue: "10001")
        }
        createTable(tableName: "horc_dm_source", remarks: "数据映射值映射来源") {
            column(name: "mapping_source_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_dm_source_PK")
            }
            column(name: "mapping_target_id", type: "bigint", remarks: "映射目标ID") {
                constraints(nullable: "false")
            }
            column(name: "mapping_field", type: "varchar(" + 80 * weight + ")", remarks: "映射来源字段") {
                constraints(nullable: "false")
            }
            column(name: "source_value", type: "varchar(" + 240 * weight + ")", remarks: "映射来源值") {
                constraints(nullable: "false")
            }
            column(name: "comparison_type", type: "varchar(30)", defaultValue: "EQUAL", remarks: "比较判定条件类型") {
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
        addUniqueConstraint(columnNames: "mapping_target_id,source_value", tableName: "horc_dm_source", constraintName: "horc_dm_source_U1")
    }


}