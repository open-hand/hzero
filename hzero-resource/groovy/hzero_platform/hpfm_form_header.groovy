package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_form_header.groovy') {
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-11-hpfm_form_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_form_header_s', startValue:"1")
        }
        createTable(tableName: "hpfm_form_header", remarks: "表单配置头") {
            column(name: "form_header_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "form_code", type: "varchar(" + 30 * weight + ")", remarks: "表单编码")  {constraints(nullable:"false")}
            column(name: "form_name", type: "varchar(" + 255 * weight + ")", remarks: "表单名称")  {constraints(nullable:"false")}
            column(name: "form_group_code", type: "varchar(" + 30 * weight + ")", remarks: "表单归类，HPFM.FORM_GROUP")  {constraints(nullable:"false")}
            column(name: "form_description", type: "varchar(" + 480 * weight + ")", remarks: "表单描述")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1",remarks: "是否启用 1:启用 0：不启用")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint", defaultValue: "0",remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint", defaultValue:"1", remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint", defaultValue:"-1", remarks: "")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime", defaultValueComputed:"CURRENT_TIMESTAMP", remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint", defaultValue:"-1", remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime", defaultValueComputed:"CURRENT_TIMESTAMP", remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"form_code,tenant_id",tableName:"hpfm_form_header",constraintName: "hpfm_form_header_u1")
    }
}