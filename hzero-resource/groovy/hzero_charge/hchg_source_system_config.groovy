package script.db

databaseChangeLog(logicalFilePath: 'script/db/hchg_source_system_config.groovy') {
    changeSet(author: "bo.he02@hand-china.com", id: "2020-03-02_hchg_source_system_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hchg_source_system_config_S', startValue: "10001")
        }
        createTable(tableName: "hchg_source_system_config", remarks: "计费来源系统配置表") {
            column(name: "source_system_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hchg_source_system_config_PK")
            }
            column(name: "system_num", type: "varchar(" + 60 * weight + ")", remarks: "来源系统编码") {
                constraints(nullable: "false")
            }
            column(name: "system_name", type: "varchar(" + 120 * weight + ")", remarks: "来源系统名称") {
                constraints(nullable: "false")
            }
            column(name: "callback_url", type: "varchar(" + 240 * weight + ")", remarks: "账单回调url") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注")
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
        addUniqueConstraint(columnNames: "system_num,tenant_id", tableName: "hchg_source_system_config", constraintName: "hchg_source_system_config_U1")

    }


}