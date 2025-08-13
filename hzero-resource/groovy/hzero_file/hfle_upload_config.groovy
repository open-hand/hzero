package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_upload_config.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hfle_upload_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_upload_config_s', startValue:"1")
        }
        createTable(tableName: "hfle_upload_config", remarks: "文件上传配置") {
            column(name: "upload_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "bucket_name", type: "varchar(" + 60 * weight + ")",  remarks: "文件目录")  {constraints(nullable:"false")}  
            column(name: "directory", type: "varchar(" + 60 * weight + ")",  remarks: "上传目录")   
            column(name: "content_type", type: "varchar(" + 240 * weight + ")",  remarks: "文件分类，值集HFLE.CONTENT_TYPE")   
            column(name: "storage_unit", type: "varchar(" + 30 * weight + ")",  remarks: "存储大小限制单位，值集HFLE.STORAGE_UNTT,KB/MB")  {constraints(nullable:"false")}  
            column(name: "storage_size", type: "int",  remarks: "存储大小")  {constraints(nullable:"false")}  
            column(name: "file_format", type: "varchar(" + 240 * weight + ")",  remarks: "文件格式，文件分类子值集HFLE.FILE_FORMAT")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"directory,bucket_name,tenant_id",tableName:"hfle_upload_config",constraintName: "hfle_upload_config_u1")
    }
}