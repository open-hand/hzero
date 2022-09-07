package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_property_type.groovy') {
changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_property_type") {
def weight = 1
if(helper.isSqlServer()){
weight = 2
} else if(helper.isOracle()){
weight = 3
}
if(helper.dbType().isSupportSequence()){
createSequence(sequenceName: 'hiot_property_type_s', startValue:"1")
}
createTable(tableName: "hiot_property_type", remarks: "数据点类型") {
column(name: "TYPE_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
column(name: "TYPE_CODE", type: "varchar(" + 30 * weight + ")",  remarks: "编码")  {constraints(nullable:"false")}  
column(name: "TYPE_NAME", type: "varchar(" + 45 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}
    column(name: "CATEGORY", type: "varchar(" + 30 * weight + ")", remarks: "分类, 取自快码 HIOT.PROPERTY_TYPE_CATEGORY") { constraints(nullable: "false") }
    column(name: "DATA_TYPE", type: "varchar(" + 30 * weight + ")",  remarks: "数据类型, 取自快码 HIOT.DATA_TYPE")  {constraints(nullable:"false")}
column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")",  remarks: "说明")   
column(name: "TENANT_ID", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
}

        
            addUniqueConstraint(columnNames:"TYPE_CODE,TENANT_ID",tableName:"hiot_property_type",constraintName: "hiot_property_type_u1")
            }
}