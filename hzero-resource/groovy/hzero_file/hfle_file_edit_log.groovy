package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_file_edit_log.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-08-hfle_file_edit_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_file_edit_log_s', startValue:"1")
        }
        createTable(tableName: "hfle_file_edit_log", remarks: "文件编辑日志") {
            column(name: "log_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "file_id", type: "bigint",  remarks: "文件Id，hfle_file.file_id")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "用户Id，iam_user.id")  {constraints(nullable:"false")}  
            column(name: "edit_type", type: "varchar(" + 30 * weight + ")",  remarks: "编辑类型，值集：HFLE.FILE.EDIT_TYPE")   
            column(name: "change_log", type: "longtext",  remarks: "变更记录")   
            column(name: "change_date", type: "datetime",  remarks: "变更时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hfle_file_edit_log", indexName: "hfle_file_edit_log_n1") {
            column(name: "file_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hfle_file_edit_log") {
        addColumn(tableName: 'hfle_file_edit_log') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}