package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_server_config_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-08-hfle_server_config_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_server_config_line_s', startValue:"1")
        }
        createTable(tableName: "hfle_server_config_line", remarks: "服务器上传配置明细") {
            column(name: "config_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "config_id", type: "bigint",  remarks: "服务器上传配置ID")  {constraints(nullable:"false")}  
            column(name: "source_id", type: "bigint",  remarks: "来源ID，根据来源类型判断关联的表")  {constraints(nullable:"false")}  
            column(name: "root_dir", type: "varchar(" + 240 * weight + ")",  remarks: "根目录")   
            column(name: "enabled_flag", type: "tinyint",  remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"config_id,source_id",tableName:"hfle_server_config_line",constraintName: "hfle_server_config_line_u1")
    }
}