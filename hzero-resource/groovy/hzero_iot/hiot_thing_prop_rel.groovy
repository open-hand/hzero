package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_thing_prop_rel.groovy') {
changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_thing_prop_rel") {
def weight = 1
if(helper.isSqlServer()){
weight = 2
} else if(helper.isOracle()){
weight = 3
}
if(helper.dbType().isSupportSequence()){
createSequence(sequenceName: 'hiot_thing_prop_rel_s', startValue:"1")
}
createTable(tableName: "hiot_thing_prop_rel", remarks: "设备与数据点关系") {
column(name: "REL_ID", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
column(name: "THING_ID", type: "bigint",  remarks: "设备ID, hiot_thing.THING_ID")  {constraints(nullable:"false")}  
column(name: "ITEM_ID", type: "bigint",  remarks: "数据点ID, hiot_property.PROPERTY_ID")  {constraints(nullable:"false")}  
column(name: "TENANT_ID", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
}

        
                addUniqueConstraint(columnNames:"THING_ID,ITEM_ID",tableName:"hiot_thing_prop_rel",constraintName: "hiot_thing_prop_rel_u1")
        }
}