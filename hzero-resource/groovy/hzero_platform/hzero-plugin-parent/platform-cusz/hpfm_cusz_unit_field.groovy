package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_unit_field.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_unit_field") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_unit_field_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_unit_field") {
            column(name:"id",type:"bigint",autoIncrement:"true",remarks:"表ID，主键，供其他表做外键"){
                constraints(primaryKey: true)
            }
            column(name:"tenant_id",type:"bigint",remarks:"租户ID"){
                constraints(nullable:false)
            }
            column(name:"unit_id",type:"bigint",remarks:"单元ID"){
                constraints(nullable:false)
            }
            column(name:"model_id",type:"bigint",remarks:"模型ID"){
                constraints(nullable:false)
            }
            column(name:"field_id",type:"bigint",remarks:"模型字段ID"){
                constraints(nullable:false)
            }
            column(name:"field_name",type:"varchar(" + 255 * weight + ")",remarks:"模型字段编码"){
                constraints(nullable:false)
            }
            column(name:"field_alias",type:"varchar(" + 255 * weight + ")",remarks:"字段别名")
            column(name:"field_editable",type:"smallint",remarks:"是否可编辑，-1条件控制"){
                constraints(nullable:false)
            }
            column(name:"field_required",type:"smallint",remarks:"是否必输，-1条件控制"){
                constraints(nullable:false)
            }
            column(name:"form_col",type:"smallint",remarks:"表单列序号")
            column(name:"form_row",type:"smallint",remarks:"表单行序号")
            column(name:"grid_seq",type:"smallint",remarks:"表格列排序号")
            column(name:"grid_width",type:"smallint",remarks:"表格列宽度")
            column(name:"grid_fixed",type:"varchar(" + 30 * weight + ")",remarks:"表格冻结配置")
            column(name:"render_options",type:"varchar(" + 30 * weight + ")",defaultValue:"WIDGET",remarks:"渲染方式"){
                constraints(nullable:false)
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames:"unit_id,field_id,tenant_id",tableName:"hpfm_cusz_unit_field",constraintName: "hpfm_cusz_unit_field_U1")
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-06_hpfm_cusz_unit_field") {
        addColumn(tableName: 'hpfm_cusz_unit_field') {
            column(name: "field_visible",  type: "smallint", defaultValue: "1", remarks: "是否显示字段,-1条件控制"){
                constraints(nullable:"false")
            }
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-08_hpfm_cusz_unit_field") {
        addColumn(tableName: 'hpfm_cusz_unit_field') {
            column(name: "label_col",  type:"smallint", remarks: "label列数")
        }
        addColumn(tableName: 'hpfm_cusz_unit_field') {
            column(name: "wrapper_col",  type:"smallint", remarks: "wrapper列数")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-08_hpfm_cusz_unit_field_index") {
        dropUniqueConstraint(tableName: 'hpfm_cusz_unit_field', constraintName: 'hpfm_cusz_unit_field_U1')
        createIndex(tableName: "hpfm_cusz_unit_field", indexName: "hpfm_cusz_unit_field_index1") {
            column(name: "unit_id")
            column(name: "field_id")
            column(name: "tenant_id")
        }
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-02-19-hpfm_cusz_unit_field_default") {
        dropDefaultValue(tableName: "hpfm_cusz_unit_field", columnName: 'field_visible')
        addDefaultValue(tableName: "hpfm_cusz_unit_field",columnName:"field_visible",columnDataType:"smallint", defaultValue:"-1")
        addDefaultValue(tableName: "hpfm_cusz_unit_field",columnName:"field_editable",columnDataType:"smallint", defaultValue:"-1")
        addDefaultValue(tableName: "hpfm_cusz_unit_field",columnName:"field_required",columnDataType:"smallint", defaultValue:"-1")
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-03-04_hpfm_cusz_unit_field_add_u1_code") {
        addColumn(tableName: 'hpfm_cusz_unit_field') {
            column(name: "field_code",  type:"varchar(" + 255 * weight + ")", remarks: "字段编码")
        }
        addUniqueConstraint(columnNames: "unit_id,field_id,field_code,tenant_id", tableName: "hpfm_cusz_unit_field", constraintName: "hpfm_cusz_unit_field_U1")
    }

    changeSet(author: "xiangyu.qi01@hand-china.com", id: "2020-07-27_hpfm_cusz_unit_field_add_col_span") {
        addColumn(tableName: 'hpfm_cusz_unit_field') {
            column(name: "col_span",  type:"int", remarks: "跨列配置")
        }
    }

}
