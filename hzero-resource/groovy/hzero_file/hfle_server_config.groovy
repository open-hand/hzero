package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_server_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-08-hfle_server_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_server_config_s', startValue:"1")
        }
        createTable(tableName: "hfle_server_config", remarks: "服务器上传配置") {
            column(name: "config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "config_code", type: "varchar(" + 30 * weight + ")",  remarks: "配置编码")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   
            column(name: "source_type", type: "varchar(" + 30 * weight + ")",  remarks: "来源类型, 值集：HFLE.SERVER.SOURCE_TYPE")  {constraints(nullable:"false")}  
            column(name: "root_dir", type: "varchar(" + 240 * weight + ")",  remarks: "默认根目录")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"config_code,tenant_id",tableName:"hfle_server_config",constraintName: "hfle_server_config_u1")
    }
}