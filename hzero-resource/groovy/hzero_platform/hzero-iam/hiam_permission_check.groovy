package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_permission_check.groovy') {
    changeSet(author: "hzero", id: "2019-12-17-hiam_permission_check") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_permission_check_s', startValue:"1")
        }
        createTable(tableName: "hiam_permission_check", remarks: "网关权限校验记录") {
            column(name: "permission_check_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
			column(name: "handle_status", type: "varchar(" + 30 * weight + ")", defaultValue: "UNTREATED",  remarks: "处理状态：HIAM.PERMISSION_CHECK.HANDLE_STATUS") {constraints(nullable:"false")}
            column(name: "permission_code", type: "varchar(" + 150 * weight + ")",  remarks: "权限编码")   
            column(name: "permission_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"API",   remarks: "权限类型：API/LOV")  {constraints(nullable:"false")}  
            column(name: "api_path", type: "varchar(" + 150 * weight + ")",  remarks: "API路径")  {constraints(nullable:"false")}  
            column(name: "api_method", type: "varchar(" + 30 * weight + ")",  remarks: "API方法")  {constraints(nullable:"false")}  
			column(name: "fd_level", type: "varchar(" + 30 * weight + ")",  remarks: "API层级")
            column(name: "service_name", type: "varchar(" + 30 * weight + ")",  remarks: "服务名称")   
            column(name: "check_state", type: "varchar(" + 60 * weight + ")",  remarks: "检查状态")  {constraints(nullable:"false")}  
            column(name: "access_token", type: "varchar(" + 60 * weight + ")",   defaultValue:"1",   remarks: "用户令牌")   
            column(name: "check_message", type: "varchar(" + 1000 * weight + ")",  remarks: "检查消息") 
            column(name: "permission_details", type: "varchar(" + 1000 * weight + ")",  remarks: "权限详细信息")   
            column(name: "route_details", type: "varchar(" + 1000 * weight + ")",  remarks: "路由详细信息")   
            column(name: "user_details", type: "longtext",  remarks: "用户详细信息")    
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
		createIndex(tableName: "hiam_permission_check", indexName: "hiam_permission_check_n1") {
            column(name: "api_path")
            column(name: "api_method")
        }
		createIndex(tableName: "hiam_permission_check", indexName: "hiam_permission_check_n2") {
            column(name: "service_name")
        }
		createIndex(tableName: "hiam_permission_check", indexName: "hiam_permission_check_n3") {
            column(name: "check_state")
        }

    }
	
	changeSet(author: 'hzero@hand-china.com', id: '2020-04-23-hiam_permission_check') {
        addColumn(tableName: 'hiam_permission_check') {
            column(name: 'menu_id', type: 'bigint', remarks: '菜单ID') {constraints(nullable: "true")}
        }
		
		createIndex(tableName: "hiam_permission_check", indexName: "hiam_permission_check_n4") {
            column(name: "menu_id")
        }
    }
}