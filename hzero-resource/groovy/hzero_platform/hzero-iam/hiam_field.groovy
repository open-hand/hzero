package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_field.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hiam_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_field_s', startValue:"1")
        }
        createTable(tableName: "hiam_field", remarks: "接口字段维护") {
            column(name: "field_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "permission_id", type: "bigint",  remarks: "接口权限ID,iam_permission.id")  {constraints(nullable:"false")}  
            column(name: "field_name", type: "varchar(" + 120 * weight + ")",  remarks: "字段名称")  {constraints(nullable:"false")}  
            column(name: "field_type", type: "varchar(" + 30 * weight + ")",  remarks: "字段类型，值集HIAM.FIELD.TYPE[NUMBER(数字),STRING(字符串)]")  {constraints(nullable:"false")}  
            column(name: "field_description", type: "varchar(" + 480 * weight + ")",  remarks: "字段描述")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"permission_id,field_name",tableName:"hiam_field",constraintName: "hiam_field_u1")
    }
}