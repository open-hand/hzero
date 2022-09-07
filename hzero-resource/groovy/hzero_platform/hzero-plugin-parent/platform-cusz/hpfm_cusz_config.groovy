package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_config.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_config_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_config") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "unit_id", type: "bigint", remarks: "个性化单元ID") {
                constraints(nullable: false)
            }
            column(name: "unit_title", type: "varchar(" + 96 * weight + ")", remarks: "单元标题")
            column(name: "form_max_col", type: "smallint", remarks: "表单列最大值")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "unit_id,tenant_id", tableName: "hpfm_cusz_config", constraintName: "hpfm_cusz_config_U1")
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-11_hpfm_cusz_config_add_user_id") {
        addColumn(tableName: 'hpfm_cusz_config') {
            column(name: "user_id",  type:"bigint", remarks: "用户id",defaultValue: "-1")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-11_hpfm_cusz_config_add_page_size") {
        addColumn(tableName: 'hpfm_cusz_config') {
            column(name: "form_page_size",  type:"int", remarks: "表格分页大小")
        }
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-22_hpfm_cusz_config_index") {
        dropUniqueConstraint(tableName: 'hpfm_cusz_config', constraintName: 'hpfm_cusz_config_U1')
        addUniqueConstraint(columnNames: "unit_id,tenant_id,user_id", tableName: "hpfm_cusz_config", constraintName: "hpfm_cusz_config_U1")
    }
}