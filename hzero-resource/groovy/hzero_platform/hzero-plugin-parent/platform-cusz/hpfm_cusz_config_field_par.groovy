package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_config_field_par.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-25_hpfm_cusz_config_field_par") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_config_field_par_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_config_field_par") {
            column(name: "config_field_par_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: false)
            }
            column(name: "config_field_id", type: "bigint", remarks: "字段id，hpfm_cusz_config_field.config_field_id") {
                constraints(nullable: false)
            }
            column(name: "param_key", type: "varchar(" + 120 * weight + ")", remarks: "参数key") {
                constraints(nullable: false)
            }
            column(name: "param_value", type: "varchar(" + 225 * weight + ")", remarks: "参数值") {
                constraints(nullable: true)
            }
            column(name: "param_type", type: "varchar(" + 30 * weight + ")", remarks: "参数类型，固定值、上下文变量、页面字段") {
                constraints(nullable: false)
            }
            column(name: "param_unit_id", type: "bigint", remarks: "param_type为页面字段时，参数所属单元ID") {
                constraints(nullable: true)
            }
            column(name: "param_field_id", type: "bigint", remarks: "param_type为页面字段时，参数字段ID") {
                constraints(nullable: true)
            }

            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "config_field_id,param_key,tenant_id", tableName: "hpfm_cusz_config_field_par", constraintName: "hpfm_cusz_config_field_par_U1")
    }
}