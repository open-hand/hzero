package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_form_line.groovy') {
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-11-hpfm_form_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_form_line_s', startValue:"1")
        }
        createTable(tableName: "hpfm_form_line", remarks: "表单配置行") {
            column(name: "form_line_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "form_header_id", type: "bigint", remarks: "表单配置头表")  {constraints(nullable:"false")}
            column(name: "item_code", type: "varchar(" + 30 * weight + ")", remarks: "配置项编码")  {constraints(nullable:"false")}
            column(name: "item_name", type: "varchar(" + 255 * weight + ")", remarks: "配置项名称")  {constraints(nullable:"false")}
            column(name: "item_description", type: "varchar(" + 480 * weight + ")", remarks: "配置项说明")
            column(name: "order_seq", type: "bigint", remarks: "排序号")  {constraints(nullable:"false")}
            column(name: "item_type_code", type: "varchar(" + 30 * weight + ")", remarks: "配置类型，HPFM.ITEM_TYPE")  {constraints(nullable:"false")}
            column(name: "required_flag", type: "tinyint", defaultValue: "0",remarks: "是否必输 1:必输 0：不必输")  {constraints(nullable:"false")}
            column(name: "default_value", type: "varchar(" + 255 * weight + ")", remarks: "默认值")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1",remarks: "是否启用 1:启用 0：不启用")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint", defaultValue:"1", remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint", defaultValue:"-1", remarks: "")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime", defaultValueComputed:"CURRENT_TIMESTAMP", remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint", defaultValue:"-1", remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime", defaultValueComputed:"CURRENT_TIMESTAMP", remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"form_header_id,item_code,tenant_id",tableName:"hpfm_form_line",constraintName: "hpfm_form_line_u1")
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-01-03-hpfm_form_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_form_line') {
            column(name: "updatable_flag", type: "tinyint", defaultValue: "1", remarks: "是否允许更新") {constraints(nullable:"false")}
        }
        addColumn(tableName: 'hpfm_form_line') {
            column(name: "value_constraint", type: "varchar(" + 480 * weight + ")", remarks: "值约束，正则表达式")
        }
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-08-06-hpfm_form_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_form_line') {
            column(name: "value_set", type: "varchar(" + 80 * weight + ")", remarks: "值集/值集视图编码")
        }
    }
}
