package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_field_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hiam_field_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_field_permission_s', startValue:"1")
        }
        createTable(tableName: "hiam_field_permission", remarks: "接口字段权限维护") {
            column(name: "field_permission_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "permission_dimension", type: "varchar(" + 30 * weight + ")",  remarks: "权限维度,值集HIAM.FIELD.PERMISSION_DIMENSION[USER,ROLE]")  {constraints(nullable:"false")}  
            column(name: "dimension_value", type: "bigint",  remarks: "维度值[USER(用户ID),ROLE(角色ID)]")  {constraints(nullable:"false")}  
            column(name: "field_id", type: "bigint",  remarks: "字段ID，hiam_field.field_id")  {constraints(nullable:"false")}  
            column(name: "permission_type", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]")  {constraints(nullable:"false")}  
            column(name: "permission_rule", type: "varchar(" + 60 * weight + ")",  remarks: "权限规则（脱敏预留）")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"permission_dimension,dimension_value,field_id,tenant_id",tableName:"hiam_field_permission",constraintName: "hiam_field_permission_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-03-16-hiam_field_permission') {
        addColumn(tableName: 'hiam_field_permission') {
            column(name: 'data_source',  type: 'varchar(30)', defaultValue: "DEFAULT", remarks: '数据来源') {constraints(nullable: "false")}
        }
    }
}