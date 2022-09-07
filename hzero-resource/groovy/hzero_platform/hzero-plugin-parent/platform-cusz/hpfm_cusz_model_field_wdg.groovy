package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_model_field_wdg.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-hpfm_cusz_model_field_wdg") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_model_field_wdg_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_model_field_wdg") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "field_id", type: "bigint", remarks: "模型字段表主键") {
                constraints(nullable: false)
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID",defaultValue: "0") {
                constraints(nullable: false)
            }
            column(name: "field_widget", type: "varchar(" + 255 * weight + ")", remarks: "字段控件类型")
            column(name: "text_max_length", type: "int", remarks: "TEXT最大程度")
            column(name: "text_min_length", type: "int", remarks: "TEXT最小程度")
            column(name: "text_area_max_line", type: "int", remarks: "文本域组件，最大行数")
            column(name: "source_code", type: "varchar(" + 255 * weight + ")", remarks: "LOV值集或者值集视图编码")
            column(name: "date_format", type: "varchar(" + 60 * weight + ")", remarks: "日期格式")
            column(name: "number_precision", type: "Tinyint", remarks: "数值精度")
            column(name: "number_min", type: "int", remarks: "数值最小值")
            column(name: "number_max", type: "bigint", remarks: "数值最大值")
            column(name: "switch_value", type: "varchar(" + 30 * weight + ")", remarks: "开关值")
            column(name: "bucket_name", type: "varchar(" + 255 * weight + ")", remarks: "上传组件，桶名")
            column(name: "bucket_directory", type: "varchar(" + 255 * weight + ")", remarks: "上传组件，桶目录")
            column(name: "link_title", type: "varchar(" + 255 * weight + ")", remarks: "链接标题")
            column(name: "link_href", type: "varchar(" + 510 * weight + ")", remarks: "链接地址")
            column(name: "link_new_window", type: "Tinyint", defaultValue: "0", remarks: "是否打开新窗口")

            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "field_id,tenant_id", tableName: "hpfm_cusz_model_field_wdg", constraintName: "hpfm_cusz_model_field_wdg_U1")
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-02-28-hpfm_cusz_model_field_wdg-add_column"){
        addColumn(tableName: 'hpfm_cusz_model_field_wdg') {
            column(name: "default_value", type: "varchar(" + 225 * weight + ")", remarks: "组件默认值")
        }
    }
}