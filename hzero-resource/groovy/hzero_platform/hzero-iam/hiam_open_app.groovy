package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_open_app.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_open_app") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_open_app_s', startValue:"1")
        }
        createTable(tableName: "hiam_open_app", remarks: "三方网站") {
            column(name: "open_app_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "app_code", type: "varchar(" + 60 * weight + ")",  remarks: "应用编码")  {constraints(nullable:"false")}  
            column(name: "app_name", type: "varchar(" + 60 * weight + ")",  remarks: "应用名称")  {constraints(nullable:"false")}  
            column(name: "app_image", type: "varchar(" + 255 * weight + ")",  remarks: "应用图片地址")  {constraints(nullable:"false")}  
            column(name: "app_id", type: "varchar(" + 120 * weight + ")",  remarks: "第三方平台方appid")  {constraints(nullable:"false")}  
            column(name: "app_key", type: "varchar(" + 120 * weight + ")",  remarks: "appid对应的授权码")  {constraints(nullable:"false")}  
            column(name: "redirect_uri", type: "varchar(" + 255 * weight + ")",  remarks: "成功授权后的回调地址")  {constraints(nullable:"false")}  
            column(name: "scope", type: "varchar(" + 255 * weight + ")",  remarks: "授权列表")   
            column(name: "authorize_path", type: "varchar(" + 255 * weight + ")",  remarks: "获取认证码的地址")   
            column(name: "token_path", type: "varchar(" + 255 * weight + ")",  remarks: "获取AccessToken的地址")   
            column(name: "refresh_token_path", type: "varchar(" + 255 * weight + ")",  remarks: "RereshToken的地址")   
            column(name: "self_path", type: "varchar(" + 255 * weight + ")",  remarks: "获取个人信息的地址")   
            column(name: "order_seq", type: "int",  remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"app_code",tableName:"hiam_open_app",constraintName: "hiam_open_app_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-08-27-hiam_open_app"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropColumn(tableName: 'hiam_open_app', columnName:'redirect_uri')
        dropColumn(tableName: 'hiam_open_app', columnName:'scope')
        dropColumn(tableName: 'hiam_open_app', columnName:'authorize_path')
        dropColumn(tableName: 'hiam_open_app', columnName:'token_path')
        dropColumn(tableName: 'hiam_open_app', columnName:'refresh_token_path')
        dropColumn(tableName: 'hiam_open_app', columnName:'self_path')
        addColumn(tableName: 'hiam_open_app') {
            column(name: "channel", type: "varchar(" + 30 * weight + ")", defaultValue: "PC", remarks: "登录渠道，值集HIAM.CHANNEL"){constraints(nullable:"false")}
        }
        dropUniqueConstraint(tableName: "hiam_open_app",constraintName: "hiam_open_app_u1")
        addUniqueConstraint(columnNames:"app_code,channel",tableName:"hiam_open_app",constraintName: "hiam_open_app_u1")
    }
	
	changeSet(author: "hzero@hand-china.com", id: "2019-11-07-hiam_open_app"){      
		addColumn(tableName: 'hiam_open_app') {
            column(name: 'organization_id', type: "bigint", defaultValue: 0, remarks: '租户ID') {
                constraints(nullable: false)
            }
        }   
    }
	
	changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hiam_open_app"){      
		addColumn(tableName: 'hiam_open_app') {
            column(name: 'sub_app_id', type: "bigint", remarks: '子应用ID') {
                constraints(nullable: true)
            }
        }   
    }
	
	changeSet(author: "hzero@hand-china.com", id: "2020-04-17-hiam_open_app"){      
		addColumn(tableName: 'hiam_open_app') {
            column(name: 'scope', type: "varchar(240)", remarks: '授权列表') {
                constraints(nullable: true)
            }
        }   
    }

    changeSet(author: "hzero@hand-china.com", id: "2021-02-24-hiam_open_app-version-001"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }

        addColumn(tableName: 'hiam_open_app') {
            column(name: 'sub_app_id_temp', type: "varchar(" + 120 * weight + ")", remarks: '子应用ID') {
                constraints(nullable: true)
            }
        }
        sql {
            "UPDATE hiam_open_app SET sub_app_id_temp = sub_app_id;"
        }
        dropColumn(tableName: 'hiam_open_app', columnName:'sub_app_id')
        renameColumn(columnDataType: "varchar(" + 120 * weight + ")", newColumnName: "sub_app_id", oldColumnName: "sub_app_id_temp", remarks: '子应用ID', tableName: 'hiam_open_app')
    }
}