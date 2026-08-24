package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_role_authority_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_role_authority_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_role_authority_line_s', startValue:"1")
        }
        createTable(tableName: "hiam_role_authority_line", remarks: "角色数据权限行定义") {
            column(name: "role_auth_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "role_auth_id", type: "bigint",  remarks: "角色数据权限ID，hiam_role_authority.role_auth_id")  {constraints(nullable:"false")}  
            column(name: "role_id", type: "bigint",  remarks: "角色ID，iam_role.id")  {constraints(nullable:"false")}  
            column(name: "auth_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型代码，HIAM.AUTHORITY_TYPE_CODE")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"role_auth_id,auth_type_code",tableName:"hiam_role_authority_line",constraintName: "hiam_role_authority_line_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-03-16-hiam_role_authority_line') {
        addColumn(tableName: 'hiam_role_authority_line') {
            column(name: 'data_source',  type: 'varchar(30)', defaultValue: "DEFAULT", remarks: '数据来源') {constraints(nullable: "false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-28-hiam_role_authority_line"){
        update(tableName:'hiam_role_authority_line'){
            column(name:'auth_type_code', value:'INV_ORGANIZATION')
            where "auth_type_code='INVORG'"
        }
        update(tableName:'hiam_role_authority_line'){
            column(name:'auth_type_code', value:'PURCHASE_ORGANIZATION')
            where "auth_type_code='PURORG'"
        }
        update(tableName:'hiam_role_authority_line'){
            column(name:'auth_type_code', value:'PURCHASE_AGENT')
            where "auth_type_code='PURAGENT'"
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hiam_role_authority_line") {
        addColumn(tableName: 'hiam_role_authority_line') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}
