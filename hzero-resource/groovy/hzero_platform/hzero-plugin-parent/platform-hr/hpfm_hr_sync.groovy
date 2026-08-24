package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_hr_sync.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com", id: "2019-10-15-hfpm_hr_sync") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_hr_sync_s', startValue:"1")
        }
        createTable(tableName: "hpfm_hr_sync", remarks: "hr基础数据同步外部系统") {
            column(name: "sync_id", type: "bigint", autoIncrement: true ,   remarks: "主键")  {constraints(primaryKey: true)}
            column(name: "sync_type_code", type: "varchar(" + 50 * weight + ")",  remarks: "同步类型 值集HPFM.HR_SYNC_TYPE 值：DD|WX")
            column(name: "app_id", type: "varchar(" + 200 * weight + ")",  remarks: "用户凭证")   
            column(name: "app_secret", type: "varchar(" + 400 * weight + ")",  remarks: "用户密钥")
            column(name: "auth_type", type: "varchar(" + 30 * weight + ")",  remarks: "授权类型，值集：HPFM.HR_AUTH_TYPE 值:SELF|THIRD")  {constraints(nullable:"false")} 
            column(name: "auth_address", type: "varchar(" + 240 * weight + ")",  remarks: "第三方授权地址") 
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 1：启用；0禁用；默认1；")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",  remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}    
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint",   defaultValue:"1",   remarks: "")   
            column(name: "CREATED_BY", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "CREATION_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   
            column(name: "LAST_UPDATED_BY", type: "bigint",   defaultValue:"0",   remarks: "")   
            column(name: "LAST_UPDATE_DATE", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")   

        }

        createIndex(tableName: "hpfm_hr_sync", indexName: "hpfm_hr_sync_n1") {
            column(name: "sync_type_code")
            column(name: "tenant_id")
            column(name: "enabled_flag")
        }
    }
}