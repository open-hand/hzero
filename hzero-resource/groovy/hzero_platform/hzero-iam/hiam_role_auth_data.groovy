package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_role_auth_data.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-06-14-hiam_role_auth_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_role_auth_data_s', startValue:"1")
        }
        createTable(tableName: "hiam_role_auth_data", remarks: "角色单据权限管理") {
            column(name: "auth_data_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "role_id", type: "bigint",  remarks: "用户ID，iam_role.role_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，HPFM.HPFM_TENANT")  {constraints(nullable:"false")}  
            column(name: "authority_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型代码，HIAM.AUTHORITY_TYPE_CODE")  {constraints(nullable:"false")}  
            column(name: "include_all_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否包含所有标识")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"role_id,tenant_id,authority_type_code",tableName:"hiam_role_auth_data",constraintName: "hiam_role_auth_data_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-03-16-hiam_role_auth_data') {
        addColumn(tableName: 'hiam_role_auth_data') {
            column(name: 'data_source',  type: 'varchar(30)', defaultValue: "DEFAULT", remarks: '数据来源') {constraints(nullable: "false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-28-hiam_role_auth_data"){
        update(tableName:'hiam_role_auth_data'){
            column(name:'authority_type_code', value:'INV_ORGANIZATION')
            where "authority_type_code='INVORG'"
        }
        update(tableName:'hiam_role_auth_data'){
            column(name:'authority_type_code', value:'PURCHASE_ORGANIZATION')
            where "authority_type_code='PURORG'"
        }
        update(tableName:'hiam_role_auth_data'){
            column(name:'authority_type_code', value:'PURCHASE_AGENT')
            where "authority_type_code='PURAGENT'"
        }
    }
}
