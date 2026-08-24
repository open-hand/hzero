package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_authority_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_user_authority_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_user_authority_line_s', startValue:"1")
        }
        createTable(tableName: "hiam_user_authority_line", remarks: "用户权限管理行") {
            column(name: "authority_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "authority_id", type: "bigint",  remarks: "权限ID，HIAM_USER_AUTHORITY.AUTHORITY_ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，HPFM.HPFM_TENANT")  {constraints(nullable:"false")}  
            column(name: "data_id", type: "bigint",  remarks: "数据ID")  {constraints(nullable:"false")}  
            column(name: "data_code", type: "varchar(" + 80 * weight + ")",  remarks: "数据代码/编码")   
            column(name: "data_name", type: "varchar(" + 360 * weight + ")",  remarks: "数据名称")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"authority_id,data_id",tableName:"hiam_user_authority_line",constraintName: "hiam_user_authority_line_u1")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-03-16-hiam_user_authority_line') {
        addColumn(tableName: 'hiam_user_authority_line') {
            column(name: 'data_source',  type: 'varchar(30)', defaultValue: "DEFAULT", remarks: '数据来源') {constraints(nullable: "false")}
        }
    }
}