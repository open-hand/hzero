package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_view_header.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov_view_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_lov_view_header_s', startValue:"1")
        }
        createTable(tableName: "hpfm_lov_view_header", remarks: "值集查询视图头表") {
            column(name: "view_header_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键 ")  {constraints(primaryKey: true)} 
            column(name: "view_code", type: "varchar(" + 30 * weight + ")",  remarks: "值集视图代码")  {constraints(nullable:"false")}  
            column(name: "view_name", type: "varchar(" + 240 * weight + ")",  remarks: "视图名称")   
            column(name: "lov_id", type: "bigint",  remarks: "值集ID,hpfm_fnd_lov.lov_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "value_field", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "值字段 ")  {constraints(nullable:"false")}  
            column(name: "display_field", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "显示字段 ")  {constraints(nullable:"false")}  
            column(name: "title", type: "varchar(" + 60 * weight + ")",  remarks: "标题")   
            column(name: "width", type: "int",  remarks: "宽度")   
            column(name: "height", type: "int",  remarks: "高度")   
            column(name: "page_size", type: "int",   defaultValue:"10",   remarks: "分页大小 ")  {constraints(nullable:"false")}  
            column(name: "delay_load_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否延迟加载 ")  {constraints(nullable:"false")}  
            column(name: "children_field_name", type: "varchar(" + 30 * weight + ")",  remarks: "树形查询子字段名")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 ")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁 ")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间  ")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "创建人 ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新时间  ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "最后更新人")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"view_code,tenant_id",tableName:"hpfm_lov_view_header",constraintName: "hpfm_lov_view_header_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-10-23-hpfm_lov_view_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov_view_header", columnName: 'view_code', newDataType: "varchar(" + 80 * weight + ")")
    }
}