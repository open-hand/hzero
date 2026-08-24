package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_dbd_menu.groovy') {
    changeSet(author: "hzero@hand-chian.com", id: "2020-04-22-hiam_user_dbd_menu") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_user_dbd_menu_s', startValue:"1")
        }
        createTable(tableName: "hiam_user_dbd_menu", remarks: "工作台用户级常用功能") {
            column(name: "dbd_menu_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "role_id", type: "bigint",  remarks: "角色ID")  {constraints(nullable:"false")}  
            column(name: "menu_id", type: "bigint",  remarks: "菜单ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"user_id,role_id,menu_id,tenant_id",tableName:"hiam_user_dbd_menu",constraintName: "hiam_dbd_user_menu_u1")
    }
}