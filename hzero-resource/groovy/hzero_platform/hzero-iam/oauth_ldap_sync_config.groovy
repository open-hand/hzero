package script.db

databaseChangeLog(logicalFilePath: 'script/db/oauth_ldap_sync_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-10-oauth_ldap_sync_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'oauth_ldap_sync_config_s', startValue: "1")
        }
        createTable(tableName: "oauth_ldap_sync_config", remarks: "Ldap同步配置表") {
            column(name: "ldap_sync_config_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "ldap_id", type: "bigint", remarks: "oauth_ldap.id") { constraints(nullable: "false") }
            column(name: "sync_job_id", type: "bigint", remarks: "定时jobId")
            column(name: "sync_type", type: "varchar(" + 30 * weight + ")", remarks: "同步类型") {
                constraints(nullable: "false")
            }
            column(name: "frequency", type: "varchar(" + 20 * weight + ")", remarks: "同步频率,值集HIAM.LDAP_SYNC_FREQUENCY") {
                constraints(nullable: "false")
            }
            column(name: "custom_filter", type: "varchar(" + 256 * weight + ")", remarks: "同步离职用户自定义筛选条件，仅开启同步离职之后生效")
            column(name: "start_date", type: "datetime", remarks: "开始时间")
            column(name: "end_date", type: "datetime", remarks: "结束时间")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "0", remarks: "是否开启Ldap自动同步，默认不开启") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户Id") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
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

        addUniqueConstraint(columnNames: "tenant_id,sync_type", tableName: "oauth_ldap_sync_config", constraintName: "oauth_ldap_sync_config_u1")
    }
}
