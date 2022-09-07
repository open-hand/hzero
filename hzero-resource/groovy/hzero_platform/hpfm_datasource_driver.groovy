package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_driver.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-08-21-hpfm_datasource_driver") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_datasource_driver_s', startValue: "1")
        }
        createTable(tableName: "hpfm_datasource_driver", remarks: "数据源驱动配置") {
            column(name: "driver_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "driver_name", type: "varchar(" + 255 * weight + ")", remarks: "驱动名称") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "varchar(" + 480 * weight + ")", remarks: "驱动描述")
            column(name: "database_type", type: "varchar(" + 30 * weight + ")", remarks: "数据库类型，值集:HPFM.DATABASE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "driver_version", type: "varchar(" + 50 * weight + ")", remarks: "驱动版本号") {
                constraints(nullable: "false")
            }
            column(name: "driver_path", type: "varchar(" + 480 * weight + ")", remarks: "驱动包路径") {
                constraints(nullable: "false")
            }
            column(name: "main_class", type: "varchar(" + 255 * weight + ")", remarks: "主类入口") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用，1启用、0禁用") {
                constraints(nullable:"false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }

        }
        addUniqueConstraint(columnNames:"tenant_id,database_type,driver_version",tableName:"hpfm_datasource_driver",constraintName: "hpfm_datasource_driver_u1")
    }
}