package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_open_account.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_user_open_account") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_user_open_account_s', startValue:"1")
        }
        createTable(tableName: "hiam_user_open_account", remarks: "用户第三方账号") {
            column(name: "open_account_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "open_id", type: "varchar(" + 60 * weight + ")",  remarks: "三方网站OpenId")  {constraints(nullable:"false")}  
            column(name: "open_name", type: "varchar(" + 60 * weight + ")",  remarks: "三方网站账户名称")  {constraints(nullable:"false")}  
            column(name: "open_app_id", type: "bigint",  remarks: "三方网站ID，关联 hiam_open_app.open_app_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2019-09-03-hiam_user_open_account"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropColumn(tableName: 'hiam_user_open_account', columnName:'user_id')
        dropColumn(tableName: 'hiam_user_open_account', columnName:'open_app_id')
        addColumn(tableName: 'hiam_user_open_account') {
            column(name: 'username', type: "varchar(" + 128 * weight + ")", remarks: '用户名称，iam_user.login_name') {
                constraints(nullable: false)
            }
        }
        addColumn(tableName: 'hiam_user_open_account') {
            column(name: 'open_app_code', type: "varchar(" + 30 * weight + ")", remarks: '三方应用编码，值集HIAM.OPEN_APP_CODE') {
                constraints(nullable: false)
            }
        }
        addUniqueConstraint(columnNames:"open_app_code,open_id",tableName:"hiam_user_open_account",constraintName: "hiam_user_open_account_u1")
        addUniqueConstraint(columnNames:"open_app_code,username",tableName:"hiam_user_open_account",constraintName: "hiam_user_open_account_u2")
    }
	
	changeSet(author: "hzero@hand-china.com", id: "2019-11-04-hiam_user_open_account"){      
        addColumn(tableName: 'hiam_user_open_account') {
            column(name: 'union_id', type: "varchar(180)", remarks: '三方UnionID') {
                constraints(nullable: true)
            }
        }
		addColumn(tableName: 'hiam_user_open_account') {
            column(name: 'image_url', type: "varchar(480)", remarks: '三方账号头像地址') {
                constraints(nullable: true)
            }
        }
        dropUniqueConstraint(constraintName: "hiam_user_open_account_u2", tableName: "hiam_user_open_account")
        addUniqueConstraint(columnNames:"open_app_code,username,union_id",tableName:"hiam_user_open_account",constraintName: "hiam_user_open_account_u2")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hiam_user_open_account") {
        addColumn(tableName: 'hiam_user_open_account') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}