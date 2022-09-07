package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_table.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_cusz_table") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_cusz_table_s', startValue:"1")
        }
        createTable(tableName: "hpfm_cusz_table", remarks: "页面个性化表格") {
            column(name: "cusz_table_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "menu_key", type: "varchar(" + 128 * weight + ")",  remarks: "菜单Key，菜单页面唯一标识")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "用户Id，iam_user.id")  {constraints(nullable:"false")}  
            column(name: "field_name", type: "varchar(" + 30 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "field_title", type: "varchar(" + 60 * weight + ")",  remarks: "表格列显示值")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户Id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "排序号")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"user_id,tenant_id,menu_key,field_name",tableName:"hpfm_cusz_table",constraintName: "hpfm_cusz_table_u1")
    }
}