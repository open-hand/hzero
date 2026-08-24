package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_permission_s', startValue:"1")
        }
        createTable(tableName: "iam_permission", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "code", type: "varchar(" + 128 * weight + ")",  remarks: "权限的标识")  {constraints(nullable:"false")}  
            column(name: "path", type: "varchar(" + 128 * weight + ")",  remarks: "权限对应的api路径")  {constraints(nullable:"false")}  
            column(name: "method", type: "varchar(" + 64 * weight + ")",  remarks: "请求的http方法")  {constraints(nullable:"false")}  
            column(name: "fd_level", type: "varchar(" + 64 * weight + ")",  remarks: "权限的层级")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 1024 * weight + ")",  remarks: "权限描述")   
            column(name: "action", type: "varchar(" + 64 * weight + ")",  remarks: "权限对应的方法名")  {constraints(nullable:"false")}  
            column(name: "fd_resource", type: "varchar(" + 128 * weight + ")",  remarks: "权限资源类型")  {constraints(nullable:"false")}  
            column(name: "public_access", type: "tinyint",  remarks: "是否公开的权限")  {constraints(nullable:"false")}  
            column(name: "login_access", type: "tinyint",  remarks: "是否需要登录才能访问的权限")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 128 * weight + ")",  remarks: "权限所在的服务名称")  {constraints(nullable:"false")}  
            column(name: "within", type: "tinyint",   defaultValue:"0",   remarks: "within")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        addUniqueConstraint(columnNames:"code",tableName:"iam_permission",constraintName: "iam_permission_u1")
        addUniqueConstraint(columnNames:"action,fd_resource,service_name",tableName:"iam_permission",constraintName: "iam_permission_u2")
        addUniqueConstraint(columnNames:"code,path,method,fd_level,service_name",tableName:"iam_permission",constraintName: "iam_permission_u3")
    }

    changeSet(author: 'bojiangzhou', id: '2019-03-08-iam_permission') {
        renameColumn(columnDataType: 'tinyint', newColumnName: "is_within", oldColumnName: "within", remarks: '是否为内部接口', tableName: 'iam_permission')
    }
	
	changeSet(author: 'bojiangzhou', id: '2019-12-11-iam_permission') {
        addColumn(tableName: 'iam_permission') {
            column(name: "tag", type: "varchar(60)", remarks: "API标签，HIAM.PERMISSION_TAG")
        }
		addColumn(tableName: 'iam_permission') {
            column(name: "sign_access", type: "tinyint", defaultValue:"0", remarks: "是否签名可访问") {constraints(nullable:"false")}
        }
    }

}