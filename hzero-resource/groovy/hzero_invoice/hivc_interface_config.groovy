package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_interface_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_interface_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_interface_config_s', startValue:"1")
        }
        createTable(tableName: "hivc_interface_config", remarks: "服务接口认证配置") {
            column(name: "interface_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "vendor_type", type: "int",   defaultValue:"0",   remarks: "供应商")  {constraints(nullable:"false")}  
            column(name: "check_order_no", type: "varchar(" + 30 * weight + ")",  remarks: "查验订单号(汇联易)")   
            column(name: "app_code", type: "varchar(" + 30 * weight + ")",  remarks: "appcode(航信)")   
            column(name: "sign_password", type: "varchar(" + 30 * weight + ")",  remarks: "签名密码(航信)")   
            column(name: "app_key", type: "varchar(" + 30 * weight + ")",  remarks: "appKey(百望)")   
            column(name: "app_secret", type: "varchar(" + 40 * weight + ")",  remarks: "appSecret(百望)")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"tenant_id",tableName:"hivc_interface_config",constraintName: "hivc_interface_config_u1")
    }
}