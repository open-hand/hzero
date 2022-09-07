package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_unit.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_unit") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_unit_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_unit") {
            column(name: "id", type: "bigint", autoIncrement: "true", remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "unit_code", type: "varchar(" + 255 * weight + ")", remarks: "个性化模型编码") {
                constraints(nullable: false)
            }
            column(name: "unit_type", type: "varchar(" + 32 * weight + ")", remarks: "单元类型,表单,表格,tab页") {
                constraints(nullable: false)
            }
            column(name: "unit_name", type: "varchar(" + 255 * weight + ")", remarks: "个性化模型名称") {
                constraints(nullable: false)
            }
            column(name: "unit_group_id", type: "bigint", remarks: "所属分组编码") {
                constraints(nullable: false)
            }
            column(name: "model_id", type: "bigint", remarks: "主模型ID") {
                constraints(nullable: false)
            }
            column(name: "sql_ids", type: "varchar(" + 300 * weight + ")", remarks: "sql_id集合,逗号分隔")
            column(name: "read_only", type: "Tinyint", defaultValue: "0", remarks: "是否只读") {
                constraints(nullable: false)
            }
            column(name: "form_max_col", type: "smallint", remarks: "表单列最大值")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "unit_code,tenant_id", tableName: "hpfm_cusz_unit", constraintName: "hpfm_cusz_unit_U1")
        createIndex(tableName: "hpfm_cusz_unit", indexName: "hpfm_cusz_unit_N1") {
            column(name: "unit_group_id")
            column(name: "tenant_id")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-08_hpfm_cusz_unit") {

        addColumn(tableName: 'hpfm_cusz_unit') {
            column(name: "label_col",  type:"smallint", remarks: "label列数")
        }
        addColumn(tableName: 'hpfm_cusz_unit') {
            column(name: "wrapper_col",  type:"smallint", remarks: "wrapper列数")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-11_hpfm_cusz_unit_add_enable_flag") {
        addColumn(tableName: 'hpfm_cusz_unit') {
            column(name: "enable_flag",  type:"tinyint", remarks: "启用标志" ,defaultValue: "1")
        }
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-14_hpfm_cusz_unit") {
        addColumn(tableName: 'hpfm_cusz_unit') {
            column(name: "con_related_unit", type: "varchar(" + 500 * weight + ")", remarks: "关联单元编码，多个以逗号隔开")
        }
    }
}