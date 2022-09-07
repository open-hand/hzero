package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_datasource") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_datasource_s', startValue: "1")
        }
        createTable(tableName: "hpfm_datasource", remarks: "数据源配置") {
            column(name: "datasource_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "ds_purpose_code", type: "varchar(" + 30 * weight + ")", remarks: "数据源用途，值集：HPFM.DATASOURCE_PURPOSE DT:数据分发 DI:数据导入 DR:数据报表") {
                constraints(nullable: "false")
            }
            column(name: "db_type", type: "varchar(" + 30 * weight + ")", remarks: "数据库类型，值集:HPFM.DATABASE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "datasource_code", type: "varchar(" + 30 * weight + ")", remarks: "数据源编码") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "varchar(" + 600 * weight + ")", remarks: "说明")
            column(name: "driver_class", type: "varchar(" + 240 * weight + ")", remarks: "数据源驱动类") {
                constraints(nullable: "false")
            }
            column(name: "datasource_url", type: "varchar(" + 600 * weight + ")", remarks: "数据源URL地址") {
                constraints(nullable: "false")
            }
            column(name: "username", type: "varchar(" + 100 * weight + ")", remarks: "用户") {
                constraints(nullable: "false")
            }
            column(name: "password_encrypted", type: "varchar(" + 300 * weight + ")", remarks: "加密密码") {
                constraints(nullable: "false")
            }
            column(name: "db_pool_type", type: "varchar(" + 30 * weight + ")", remarks: "连接池类型，独立值集：HPFM.DB_POOL_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "queryer_class", type: "varchar(" + 240 * weight + ")", remarks: "获取报表引擎查询器类名")
            column(name: "pool_class", type: "varchar(" + 240 * weight + ")", remarks: "报表引擎查询器使用的数据源连接池类名")
            column(name: "options", type: "longtext", remarks: "数据源配置选项(JSON格式）")
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用，1启用、0禁用")
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
        createIndex(tableName: "hpfm_datasource", indexName: "hpfm_datasource_u1") {
            column(name: "datasource_code")
            column(name: "tenant_id")
        }
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-15-hpfm_datasource") {
        dropNotNullConstraint(tableName: "hpfm_datasource", columnName: "driver_class", columnDataType: "varchar(" + 240 * weight + ")")
        dropNotNullConstraint(tableName: "hpfm_datasource", columnName: "datasource_url", columnDataType: "varchar(" + 600 * weight + ")")
        dropNotNullConstraint(tableName: "hpfm_datasource", columnName: "username", columnDataType: "varchar(" + 100 * weight + ")")
        dropNotNullConstraint(tableName: "hpfm_datasource", columnName: "password_encrypted", columnDataType: "varchar(" + 300 * weight + ")")
        dropNotNullConstraint(tableName: "hpfm_datasource", columnName: "db_pool_type", columnDataType: "varchar(" + 30 * weight + ")")
        addColumn(tableName: 'hpfm_datasource') {
            column(name: "ext_config", type: "longtext", remarks: "扩展数据库配置信息(JSON格式)")
        }
        addColumn(tableName: 'hpfm_datasource') {
            column(name: "driver_id", type: "bigint", remarks: "驱动Id,用于关联自定义驱动配置项")
        }
        addDefaultValue(tableName: 'hpfm_datasource', columnName: 'db_pool_type', columnDataType: "varchar(" + 30 * weight + ")", defaultValue: "No")
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-09-19-hpfm_datasource") {
        dropIndex(tableName: 'hpfm_datasource', indexName: 'hpfm_datasource_u1')
        addUniqueConstraint(columnNames:"datasource_code,tenant_id",tableName:"hpfm_datasource",constraintName: "hpfm_datasource_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-01-hpfm_datasource") {
        addColumn(tableName: 'hpfm_datasource') {
            column(name: "datasource_class", type: "varchar(" + 30 * weight + ")", defaultValue: "RDB", remarks: "数据源分类(快码：HPFM.DATASOURCE_CLASS)"){
                constraints(nullable: "false")
            }
        }
        addColumn(tableName: 'hpfm_datasource') {
            column(name: "driver_type", type: "varchar(" + 30 * weight + ")", defaultValue: "DEFAULT", remarks: "驱动类型(快码：HPFM.DATASOURCE_DRIVER_TYPE  DEFAULT:默认 CUSTOMIZE：自定义)")
        }
    }
}
