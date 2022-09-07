package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_config_field_map.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_config_field_map") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_config_field_map_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_config_field_map") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "config_field_id", type: "bigint", remarks: "个性化字段ID") {
                constraints(nullable: false)
            }
            column(name: "target_field_id", type: "bigint", remarks: "映射目标字段ID") {
                constraints(nullable: false)
            }
            column(name: "source_model_id", type: "bigint", remarks: "目标字段所属模型") {
                constraints(nullable: false)
            }
            column(name: "source_field_id", type: "bigint", remarks: "映射源字段ID") {
                constraints(nullable: false)
            }
            column(name: "source_field_alias", type:"varchar(" + 120 * weight + ")", remarks: "映射源字段别名") {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "config_field_id,target_field_id,source_field_id", tableName: "hpfm_cusz_config_field_map", constraintName: "hpfm_cusz_config_field_map_U1")

    }
}