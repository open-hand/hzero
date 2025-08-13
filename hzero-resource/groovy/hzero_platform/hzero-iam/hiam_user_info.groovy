package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_info.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_user_info") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_user_info_s', startValue:"1")
        }
        createTable(tableName: "hiam_user_info", remarks: "用户信息表") {
            column(name: "user_id", type: "bigint",  remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "company_name", type: "varchar(" + 360 * weight + ")",  remarks: "企业名称")   
            column(name: "invitation_code", type: "varchar(" + 120 * weight + ")",  remarks: "邀请码")   
            column(name: "employee_id", type: "bigint",  remarks: "员工id")   
            column(name: "text_id", type: "bigint",  remarks: "协议id")   
            column(name: "security_level_code", type: "varchar(" + 30 * weight + ")",  remarks: "密码安全等级，值集：HIAM.SECURITY_LEVEL")  {constraints(nullable:"false")}  
            column(name: "start_date_active", type: "date",  remarks: "有效日期从")  {constraints(nullable:"false")}  
            column(name: "end_date_active", type: "date",  remarks: "有效日期至")   
            column(name: "user_source", type: "tinyint",   defaultValue:"0",   remarks: "用户来源：0-由管理员创建，1-门户注册")  {constraints(nullable:"false")}  
            column(name: "phone_check_flag", type: "tinyint",   defaultValue:"0",   remarks: "手机是否已验证")  {constraints(nullable:"false")}  
            column(name: "email_check_flag", type: "tinyint",   defaultValue:"0",   remarks: "邮箱是否已验证")  {constraints(nullable:"false")}  
            column(name: "password_reset_flag", type: "tinyint",   defaultValue:"0",   remarks: "密码是否需要重置")  {constraints(nullable:"false")}  
            column(name: "default_tenant_id", type: "bigint",  remarks: "默认租户ID,hpfm_tenant.tenant_id")   
            column(name: "locked_date", type: "datetime",  remarks: "账户锁定事件")   
            column(name: "date_format", type: "varchar(" + 30 * weight + ")",   defaultValue:"YYYY-MM-DD",   remarks: "账号日期格式(年月日)")  {constraints(nullable:"false")}  
            column(name: "time_format", type: "varchar(" + 30 * weight + ")",   defaultValue:"HH:mm:ss",   remarks: "账号时间格式(时分秒)")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "birthday", type: "date",  remarks: "出生日期")   
            column(name: "nickname", type: "varchar(" + 30 * weight + ")",  remarks: "昵称")   
            column(name: "gender", type: "tinyint",  remarks: "性别, 1: 男 0: 女")   
            column(name: "country_id", type: "bigint",  remarks: "国家")   
            column(name: "region_id", type: "bigint",  remarks: "地区id，树形值集")   
            column(name: "address_detail", type: "varchar(" + 150 * weight + ")",  remarks: "详细地址")   

        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hiam_user_info") {
        addColumn(tableName: 'hiam_user_info') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-1-hiam_user_info") {
        addColumn(tableName: "hiam_user_info") {
            column(name: "sec_check_phone_flag", type: "tinyint", defaultValue: "0", remarks: "二次校验验证码是否发送给手机")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-2-hiam_user_info") {
        addColumn(tableName: "hiam_user_info") {
            column(name: "sec_check_email_flag", type: "tinyint", defaultValue: "0", remarks: "二次校验验证码是否发送给邮箱")  {constraints(nullable:"false")}
        }
    }

}